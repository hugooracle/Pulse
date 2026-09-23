param(
    [string]$KeyAlias = "pulse"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function ConvertFrom-SecureStringPlain {
    param([Parameter(Mandatory = $true)][Security.SecureString]$SecureString)

    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($SecureString)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}

function Read-ConfirmedPassword {
    while ($true) {
        $first = Read-Host "Password da chave de assinatura Pulse" -AsSecureString
        $second = Read-Host "Confirma a password" -AsSecureString
        $firstPlain = ConvertFrom-SecureStringPlain $first
        $secondPlain = ConvertFrom-SecureStringPlain $second
        try {
            if ([string]::IsNullOrWhiteSpace($firstPlain)) {
                Write-Host "A password não pode estar vazia." -ForegroundColor Yellow
                continue
            }
            if ($firstPlain -ne $secondPlain) {
                Write-Host "As passwords não coincidem. Tenta novamente." -ForegroundColor Yellow
                continue
            }
            return $firstPlain
        }
        finally {
            $secondPlain = $null
        }
    }
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
Set-Location $repoRoot

$versionsFile = Join-Path $repoRoot "gradle\libs.versions.toml"
if (-not (Test-Path $versionsFile)) {
    throw "Não foi encontrado $versionsFile"
}

$versionsText = Get-Content $versionsFile -Raw
$versionNameMatch = [regex]::Match($versionsText, '(?m)^version-name\s*=\s*"([^"]+)"')
$versionCodeMatch = [regex]::Match($versionsText, '(?m)^version-code\s*=\s*"([^"]+)"')
if (-not $versionNameMatch.Success -or -not $versionCodeMatch.Success) {
    throw "Não foi possível obter version-name/version-code de gradle/libs.versions.toml"
}

$versionName = $versionNameMatch.Groups[1].Value
$versionCode = $versionCodeMatch.Groups[1].Value
Write-Host "Pulse $versionName (versionCode $versionCode)" -ForegroundColor Cyan

$keytool = Get-Command keytool -ErrorAction SilentlyContinue
if (-not $keytool) {
    throw "keytool não encontrado. Confirma que o JDK do Android Studio está instalado e disponível no PATH."
}

$sdkCandidates = @(
    $env:ANDROID_SDK_ROOT,
    $env:ANDROID_HOME,
    (Join-Path $env:LOCALAPPDATA "Android\Sdk")
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -Unique

if (-not $sdkCandidates) {
    throw "Android SDK não encontrado. Define ANDROID_SDK_ROOT/ANDROID_HOME ou instala o SDK pelo Android Studio."
}

$sdkRoot = $sdkCandidates[0]
$buildToolsRoot = Join-Path $sdkRoot "build-tools"
if (-not (Test-Path $buildToolsRoot)) {
    throw "Android build-tools não encontrados em $buildToolsRoot"
}

$buildTools = Get-ChildItem $buildToolsRoot -Directory |
    Where-Object { $_.Name -match '^\d+(\.\d+){1,2}$' } |
    Sort-Object { [version]$_.Name } -Descending |
    Select-Object -First 1

if (-not $buildTools) {
    throw "Não foi encontrada uma versão estável de Android build-tools em $buildToolsRoot"
}

$zipalign = Join-Path $buildTools.FullName "zipalign.exe"
$apksigner = Join-Path $buildTools.FullName "apksigner.bat"
if (-not (Test-Path $zipalign)) { throw "zipalign não encontrado: $zipalign" }
if (-not (Test-Path $apksigner)) { throw "apksigner não encontrado: $apksigner" }

$keystore = Join-Path $repoRoot "pulse.jks"
$password = $null

try {
    if (-not (Test-Path $keystore)) {
        Write-Host "Não existe pulse.jks. Será criada a chave oficial de assinatura da aplicação." -ForegroundColor Yellow
        Write-Host "GUARDA este ficheiro e a password. Sem esta chave não será possível publicar atualizações assinadas com a mesma identidade." -ForegroundColor Yellow
        $password = Read-ConfirmedPassword
        $env:PULSE_SIGNING_PASSWORD = $password

        & $keytool.Source `
            -genkeypair `
            -v `
            -keystore $keystore `
            -alias $KeyAlias `
            -keyalg RSA `
            -keysize 4096 `
            -validity 10000 `
            -dname "CN=Pulse, OU=Pulse, O=Pulse, C=PT" `
            -storepass:env PULSE_SIGNING_PASSWORD `
            -keypass:env PULSE_SIGNING_PASSWORD

        if ($LASTEXITCODE -ne 0 -or -not (Test-Path $keystore)) {
            throw "Falhou a criação de pulse.jks"
        }
    }
    else {
        $securePassword = Read-Host "Password de pulse.jks" -AsSecureString
        $password = ConvertFrom-SecureStringPlain $securePassword
        if ([string]::IsNullOrWhiteSpace($password)) {
            throw "A password não pode estar vazia."
        }
        $env:PULSE_SIGNING_PASSWORD = $password
    }

    Write-Host "A gerar APK release (minificada, sem sufixo .dev)..." -ForegroundColor Cyan
    & .\gradlew.bat --stop
    & .\gradlew.bat clean
    & .\gradlew.bat :androidApp:assembleRelease --no-configuration-cache --no-daemon
    if ($LASTEXITCODE -ne 0) {
        throw "O Gradle falhou ao gerar a APK release."
    }

    $releaseDir = Join-Path $repoRoot "androidApp\build\outputs\apk\release"
    $unsignedApk = Get-ChildItem $releaseDir -Filter "*.apk" -File -ErrorAction Stop |
        Where-Object { $_.Name -match 'release' } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

    if (-not $unsignedApk) {
        throw "Não foi encontrada a APK release em $releaseDir"
    }

    $distDir = Join-Path $repoRoot "dist"
    New-Item -ItemType Directory -Path $distDir -Force | Out-Null

    $alignedApk = Join-Path $distDir "Pulse-$versionName-aligned.apk"
    $finalApk = Join-Path $distDir "Pulse-$versionName.apk"
    $shaFile = "$finalApk.sha256"

    Remove-Item $alignedApk, $finalApk, $shaFile -Force -ErrorAction SilentlyContinue

    & $zipalign -f -p 4 $unsignedApk.FullName $alignedApk
    if ($LASTEXITCODE -ne 0) {
        throw "zipalign falhou."
    }

    & $apksigner sign `
        --ks $keystore `
        --ks-key-alias $KeyAlias `
        --ks-pass env:PULSE_SIGNING_PASSWORD `
        --key-pass env:PULSE_SIGNING_PASSWORD `
        --out $finalApk `
        $alignedApk
    if ($LASTEXITCODE -ne 0) {
        throw "apksigner falhou. Confirma a password e o alias da chave."
    }

    & $apksigner verify --verbose --print-certs $finalApk
    if ($LASTEXITCODE -ne 0) {
        throw "A verificação criptográfica da APK falhou."
    }

    Remove-Item $alignedApk -Force -ErrorAction SilentlyContinue

    $hash = (Get-FileHash $finalApk -Algorithm SHA256).Hash.ToLowerInvariant()
    "$hash  $(Split-Path $finalApk -Leaf)" | Set-Content $shaFile -Encoding ascii

    Write-Host "" 
    Write-Host "RELEASE PRONTA" -ForegroundColor Green
    Write-Host "APK: $finalApk"
    Write-Host "SHA256: $hash"
    Write-Host "Checksum: $shaFile"
}
finally {
    $env:PULSE_SIGNING_PASSWORD = $null
    $password = $null
}
