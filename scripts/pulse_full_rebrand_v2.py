#!/usr/bin/env python3
from __future__ import annotations

import re
import shutil
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LEGAL = {"LICENSE", "LICENSE.md", "NOTICE.md"}
MIGRATION_PREFIX = "pulse_full_rebrand"
TEXT_EXT = {".kt", ".kts", ".java", ".xml", ".md", ".txt", ".json", ".yml", ".yaml", ".toml", ".properties", ".gradle", ".pro", ".conf", ".cfg", ".sh", ".ps1", ".bat", ".html", ".css", ".js", ".ts", ".swift", ".plist", ".py", ".svg"}
TEXT_NAMES = {"gradlew", "gradlew.bat", ".gitignore", ".gitattributes", "Dockerfile", "conveyor.conf", "Gemfile", "Podfile"}

REPLACEMENTS = [
    ("com.maxrave.simpmusic", "pt.pulse.app"),
    ("org.simpmusic", "pt.pulse.service"),
    ("com.simpmusic", "pt.pulse"),
    ("com.maxrave", "pt.pulse.core"),
    ("maxrave-dev/SimpMusic", "hugooracle/Pulse"),
    ("maxrave-dev/simpmusic", "hugooracle/Pulse"),
    ("SimpMusic", "Pulse"),
    ("Simpmusic", "Pulse"),
    ("simpMusic", "pulse"),
    ("SIMPMUSIC", "PULSE"),
    ("simpmusic", "pulse"),
]


def ignored(path: Path) -> bool:
    return ".git" in path.parts or path.name.startswith(MIGRATION_PREFIX)


def text_file(path: Path) -> bool:
    return path.suffix.lower() in TEXT_EXT or path.name in TEXT_NAMES


def remove(rel: str) -> None:
    path = ROOT / rel
    if path.is_dir():
        shutil.rmtree(path)
    elif path.exists():
        path.unlink()


def clean_inherited_material() -> None:
    for rel in [".claude", ".github", "fastlane", "asset", "CLAUDE.md", "PULSE_UPSTREAM.md", "PULSE_PLAN.md"]:
        remove(rel)


def replace_text(value: str) -> str:
    for old, new in REPLACEMENTS:
        value = value.replace(old, new)
    return value


def mapped_path(rel: Path) -> Path:
    value = rel.as_posix()
    value = value.replace("com/maxrave/simpmusic", "pt/pulse/app")
    value = value.replace("org/simpmusic", "pt/pulse/service")
    value = value.replace("com/simpmusic", "pt/pulse")
    value = value.replace("com/maxrave", "pt/pulse/core")
    value = value.replace("SimpMusic", "Pulse").replace("Simpmusic", "Pulse").replace("simpMusic", "pulse").replace("SIMPMUSIC", "PULSE").replace("simpmusic", "pulse")
    return Path(value)


def pt_pt(value: str) -> str:
    pairs = [
        ("Baixando", "A descarregar"), ("baixando", "a descarregar"),
        ("Baixados", "Descarregados"), ("baixados", "descarregados"),
        ("Baixadas", "Descarregadas"), ("baixadas", "descarregadas"),
        ("Baixado", "Descarregado"), ("baixado", "descarregado"),
        ("Baixar", "Descarregar"), ("baixar", "descarregar"),
        ("Compartilhar", "Partilhar"), ("compartilhar", "partilhar"),
        ("Compartilhado", "Partilhado"), ("compartilhado", "partilhado"),
        ("Aplicativo", "Aplicação"), ("aplicativo", "aplicação"),
        ("Deletar", "Eliminar"), ("deletar", "eliminar"),
        ("Excluir", "Remover"), ("excluir", "remover"),
        ("Tocador", "Leitor"), ("tocador", "leitor"),
        ("Tela", "Ecrã"), ("tela", "ecrã"),
        ("Arquivo", "Ficheiro"), ("arquivo", "ficheiro"),
        ("Arquivos", "Ficheiros"), ("arquivos", "ficheiros"),
        ("Celular", "Telemóvel"), ("celular", "telemóvel"),
        ("Padrão", "Predefinido"), ("padrão", "predefinido"),
        ("Configurações", "Definições"), ("configurações", "definições"),
        ("Usuário", "Utilizador"), ("usuário", "utilizador"),
        ("Usuários", "Utilizadores"), ("usuários", "utilizadores"),
        ("Senha", "Palavra-passe"), ("senha", "palavra-passe"),
        ("Compartilhe", "Partilhe"), ("compartilhe", "partilhe"),
    ]
    for old, new in pairs:
        value = value.replace(old, new)
    return value


def canonical_pt_pt() -> None:
    resources = ROOT / "composeApp/src/commonMain/composeResources"
    default = resources / "values/strings.xml"
    source = resources / "values-pt/strings.xml"
    if default.exists() and source.exists():
        base_root = ET.parse(default).getroot()
        pt_root = ET.parse(source).getroot()
        translated = {e.attrib.get("name"): e.text for e in pt_root.findall("string") if e.text}
        for element in base_root.findall("string"):
            name = element.attrib.get("name")
            if name in translated:
                element.text = pt_pt(translated[name])
        ET.indent(base_root, space="    ")
        ET.ElementTree(base_root).write(default, encoding="utf-8", xml_declaration=True)
    if resources.exists():
        for child in list(resources.iterdir()):
            if child.is_dir() and child.name.startswith("values-"):
                shutil.rmtree(child)


def transform_contents() -> None:
    for path in sorted(ROOT.rglob("*")):
        if ignored(path) or not path.is_file() or path.name in LEGAL or not text_file(path):
            continue
        try:
            current = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        changed = replace_text(current)
        if changed != current:
            path.write_text(changed, encoding="utf-8")


def transform_paths() -> None:
    files = [p for p in ROOT.rglob("*") if p.is_file() and not ignored(p)]
    for path in sorted(files, key=lambda p: len(p.parts), reverse=True):
        rel = path.relative_to(ROOT)
        if rel.name in LEGAL:
            continue
        target_rel = mapped_path(rel)
        if target_rel == rel:
            continue
        target = ROOT / target_rel
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.exists():
            target.unlink()
        path.replace(target)
    for directory in sorted([p for p in ROOT.rglob("*") if p.is_dir() and not ignored(p)], key=lambda p: len(p.parts), reverse=True):
        try:
            directory.rmdir()
        except OSError:
            pass


def product_specific_cleanup() -> None:
    # There is no Pulse web domain yet. Keep only the custom pulse:// scheme.
    manifest = ROOT / "androidApp/src/main/AndroidManifest.xml"
    if manifest.exists():
        text = manifest.read_text(encoding="utf-8")
        text = re.sub(r'\n\s*<!-- Pulse deep link filter \(https\) -->\s*<intent-filter android:autoVerify="true">.*?</intent-filter>', "", text, flags=re.S)
        manifest.write_text(text, encoding="utf-8")

    app = ROOT / "composeApp/src/commonMain/kotlin/pt/pulse/app/App.kt"
    if app.exists():
        text = app.read_text(encoding="utf-8")
        text = text.replace('data.host == "pulse.org" || data.scheme == "pulse"', 'data.scheme == "pulse"')
        text = text.replace('Logger.d("MainActivity", "pulse.org deep link, appPath: $appPath")', 'Logger.d("MainActivity", "Pulse deep link, appPath: $appPath")')
        text = re.sub(r'^\s*//.*pulse\.org.*\n', '', text, flags=re.M)
        text = text.replace('openUrl("https://pulse.org/download")', 'Unit')
        app.write_text(text, encoding="utf-8")

    # Desktop handler should preserve the custom scheme rather than inventing a web domain.
    desktop = ROOT / "composeApp/src/jvmMain/kotlin/pt/pulse/app/DesktopDeepLinkHandler.kt"
    if desktop.exists():
        text = desktop.read_text(encoding="utf-8")
        text = re.sub(r'https://pulse\.org/app/', 'pulse://', text)
        text = re.sub(r'^\s*//.*pulse\.org.*\n', '', text, flags=re.M)
        desktop.write_text(text, encoding="utf-8")

    # Old website/chart/help links are removed; these actions become inert until Pulse services exist.
    for rel in [
        "composeApp/src/commonMain/kotlin/pt/pulse/app/ui/screen/other/CreditScreen.kt",
        "composeApp/src/commonMain/kotlin/pt/pulse/app/ui/screen/other/SearchScreen.kt",
        "composeApp/src/commonMain/kotlin/pt/pulse/app/ui/component/GridLibraryPlaylist.kt",
        "composeApp/src/jvmMain/kotlin/pt/pulse/app/expect/ui/Cookies.jvm.kt",
    ]:
        path = ROOT / rel
        if path.exists():
            text = path.read_text(encoding="utf-8")
            text = re.sub(r'\b(?:openUrl|uriHandler\.openUri)\("https?://(?:www\.|chart\.)?pulse\.org[^"\n]*"\)', 'Unit', text)
            path.write_text(text, encoding="utf-8")

    # Import screen: remove the inherited converter URL while keeping local import guidance.
    setting = ROOT / "composeApp/src/commonMain/kotlin/pt/pulse/app/ui/screen/home/SettingScreen.kt"
    if setting.exists():
        text = setting.read_text(encoding="utf-8")
        text = text.replace('https://www.pulse.org/tools', '')
        setting.write_text(text, encoding="utf-8")

    strings = ROOT / "composeApp/src/commonMain/composeResources/values/strings.xml"
    if strings.exists():
        text = strings.read_text(encoding="utf-8")
        text = text.replace('https://www.pulse.org/tools', '').replace('pulse.org', 'Pulse')
        text = re.sub(r'<string name="maxrave_dev">.*?</string>', '<string name="maxrave_dev">Pulse</string>', text)
        text = re.sub(r'<string name="credit_app">.*?</string>', '<string name="credit_app">Pulse é uma aplicação de música multiplataforma, sem publicidade integrada, com pesquisa, biblioteca, reprodução em segundo plano e funcionalidades avançadas de áudio.</string>', text, flags=re.S)
        strings.write_text(text, encoding="utf-8")

    conveyor = ROOT / "conveyor.conf"
    if conveyor.exists():
        text = conveyor.read_text(encoding="utf-8").replace('vendor      = "maxrave-dev"', 'vendor      = "Pulse"')
        text = re.sub(r'^\s*//.*pulse\.org.*\n', '', text, flags=re.M)
        conveyor.write_text(text, encoding="utf-8")


def visual_branding() -> None:
    res = ROOT / "androidApp/src/main/res"
    if res.exists():
        for directory in res.glob("mipmap-*"):
            if directory.is_dir():
                shutil.rmtree(directory)
        vector = '''<?xml version="1.0" encoding="utf-8"?>\n<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">\n<path android:fillColor="#101114" android:pathData="M0,0h108v108h-108z"/>\n<path android:fillColor="#FFFFFF" android:pathData="M31,24h24c17,0 28,9 28,24c0,16 -11,25 -29,25h-9v15h-14zM45,37v23h9c9,0 14,-4 14,-12c0,-7 -5,-11 -14,-11z"/>\n</vector>\n'''
        (res / "drawable/pulse_icon.xml").write_text(vector, encoding="utf-8")
        manifest = ROOT / "androidApp/src/main/AndroidManifest.xml"
        text = manifest.read_text(encoding="utf-8")
        text = re.sub(r'android:icon="@[^"]+"', 'android:icon="@drawable/pulse_icon"', text)
        text = re.sub(r'\s*android:roundIcon="@[^"]+"', '', text)
        manifest.write_text(text, encoding="utf-8")

    common = ROOT / "composeApp/src/commonMain/composeResources/drawable"
    if common.exists():
        for old in ["app_icon.png", "circle_app_icon.png"]:
            path = common / old
            if path.exists():
                path.unlink()
        vector = '''<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">\n<path android:fillColor="#101114" android:pathData="M0,0h108v108h-108z"/>\n<path android:fillColor="#FFFFFF" android:pathData="M31,24h24c17,0 28,9 28,24c0,16 -11,25 -29,25h-9v15h-14zM45,37v23h9c9,0 14,-4 14,-12c0,-7 -5,-11 -14,-11z"/>\n</vector>\n'''
        (common / "app_icon.xml").write_text(vector, encoding="utf-8")
        (common / "circle_app_icon.xml").write_text(vector, encoding="utf-8")
    remove("composeApp/appimage")


def docs() -> None:
    (ROOT / "README.md").write_text("""# Pulse\n\n**Pulse** é uma aplicação multiplataforma para ouvir, descobrir e organizar música.\n\n## Tecnologia\n\n- Kotlin\n- Compose Multiplatform\n- Media3 / ExoPlayer no Android\n- arquitetura modular\n\nO idioma de referência da interface é Português (Portugal).\n\n## Licença\n\nConsultar `LICENSE` e `NOTICE.md`.\n""", encoding="utf-8")
    (ROOT / "CONTRIBUTING.md").write_text("""# Contribuir para o Pulse\n\nAs alterações devem manter a identidade Pulse e os textos da interface em Português (Portugal). Não devem reintroduzir branding, endpoints ou identificadores do projeto de origem.\n\nAntes de submeter alterações, confirmar compilação, testes aplicáveis e a auditoria de identidade.\n""", encoding="utf-8")
    (ROOT / "CODE_OF_CONDUCT.md").write_text("""# Código de Conduta\n\nO projeto Pulse mantém um espaço de colaboração profissional, respeitador e inclusivo. As contribuições devem ser técnicas, construtivas e respeitadoras.\n""", encoding="utf-8")
    (ROOT / "NOTICE.md").write_text("""# Avisos legais\n\nPulse é um trabalho derivado de SimpMusic e inclui código originalmente distribuído sob a GNU GPL. Os direitos de autor, licenças e avisos de terceiros aplicáveis são preservados nos termos da licença.\n\nA identidade, o nome, os recursos visuais, os identificadores técnicos e a documentação do produto Pulse são independentes da identidade do projeto de origem.\n""", encoding="utf-8")


def audit() -> None:
    forbidden_text = ["simpmusic", "simpmusic.org", "simpmusic://", "pulse.org", "com.maxrave.simpmusic", "org.simpmusic", "com.simpmusic"]
    failures: list[str] = []
    for path in sorted(ROOT.rglob("*")):
        if ignored(path):
            continue
        rel = path.relative_to(ROOT).as_posix()
        if path.name not in LEGAL and "simpmusic" in rel.lower():
            failures.append(f"caminho: {rel}")
        if not path.is_file() or path.name in LEGAL or not text_file(path):
            continue
        try:
            low = path.read_text(encoding="utf-8").lower()
        except UnicodeDecodeError:
            continue
        for token in forbidden_text:
            if token in low:
                failures.append(f"{rel}: {token}")
                break
    if failures:
        print("\n".join(failures[:250]))
        raise SystemExit(f"Auditoria falhou: {len(failures)} ocorrência(s)")


def main() -> None:
    clean_inherited_material()
    canonical_pt_pt()
    transform_contents()
    transform_paths()
    product_specific_cleanup()
    visual_branding()
    docs()
    audit()
    print("Auditoria de identidade Pulse concluída.")


if __name__ == "__main__":
    main()
