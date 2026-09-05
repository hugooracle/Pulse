#!/usr/bin/env python3
from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

# Legal attribution is deliberately isolated from the product/source rebrand.
LEGAL_FILES = {"LICENSE", "LICENSE.md", "NOTICE.md"}

TEXT_SUFFIXES = {
    ".kt", ".kts", ".java", ".xml", ".md", ".txt", ".json", ".yml", ".yaml",
    ".toml", ".properties", ".gradle", ".pro", ".conf", ".cfg", ".sh", ".ps1",
    ".bat", ".html", ".css", ".js", ".ts", ".swift", ".plist", ".py", ".svg",
}
TEXT_NAMES = {
    "gradlew", "gradlew.bat", ".gitignore", ".gitattributes", "Dockerfile",
    "conveyor.conf", "Gemfile", "Podfile",
}

# Specific mappings must precede broad mappings.
TEXT_REPLACEMENTS = [
    ("com.maxrave.simpmusic", "pt.pulse.app"),
    ("org.simpmusic", "pt.pulse.service"),
    ("com.simpmusic", "pt.pulse"),
    ("com.maxrave", "pt.pulse.core"),
    ("maxrave-dev/SimpMusic", "hugooracle/Pulse"),
    ("maxrave-dev/simpmusic", "hugooracle/Pulse"),
    ("maxrave-dev/core", "hugooracle/Pulse"),
    ("SimpMusic", "Pulse"),
    ("SIMPMUSIC", "PULSE"),
    ("simpmusic", "pulse"),
]

# Paths mirror the package mappings above so source layout and declarations stay aligned.
def map_relative_path(rel: Path) -> Path:
    s = rel.as_posix()
    s = s.replace("com/maxrave/simpmusic", "pt/pulse/app")
    s = s.replace("org/simpmusic", "pt/pulse/service")
    s = s.replace("com/simpmusic", "pt/pulse")
    s = s.replace("com/maxrave", "pt/pulse/core")
    s = s.replace("SimpMusic", "Pulse")
    s = s.replace("SIMPMUSIC", "PULSE")
    s = s.replace("simpmusic", "pulse")
    return Path(s)


def is_text_file(path: Path) -> bool:
    return path.suffix.lower() in TEXT_SUFFIXES or path.name in TEXT_NAMES


def replace_text(text: str) -> str:
    for old, new in TEXT_REPLACEMENTS:
        text = text.replace(old, new)
    return text


def remove_inherited_project_material() -> None:
    for rel in [
        ".claude",
        "fastlane",
        "asset",
        ".github/FUNDING.yml",
        ".github/ISSUE_TEMPLATE",
        ".github/PULL_REQUEST_TEMPLATE.md",
        "PULSE_UPSTREAM.md",
        "PULSE_PLAN.md",
    ]:
        p = ROOT / rel
        if p.is_dir():
            shutil.rmtree(p)
        elif p.exists():
            p.unlink()


def rewrite_project_docs() -> None:
    (ROOT / "README.md").write_text(
        """# Pulse\n\n"
        "**Pulse** é uma aplicação multiplataforma para ouvir, descobrir e organizar música.\n\n"
        "## Plataformas\n\n"
        "- Android\n"
        "- Desktop (estrutura multiplataforma)\n\n"
        "## Tecnologia\n\n"
        "- Kotlin\n"
        "- Compose Multiplatform\n"
        "- Media3 / ExoPlayer no Android\n"
        "- arquitetura modular\n\n"
        "## Desenvolvimento\n\n"
        "O idioma de referência da interface é Português (Portugal).\n\n"
        "## Licença\n\n"
        "Consultar `LICENSE` e `NOTICE.md`.\n"
        """,
        encoding="utf-8",
    )

    (ROOT / "CONTRIBUTING.md").write_text(
        """# Contribuir para o Pulse\n\n"
        "As alterações devem manter a identidade Pulse, os textos da interface em Português (Portugal) "
        "e não devem reintroduzir branding, endpoints ou identificadores do projeto de origem.\n\n"
        "Antes de submeter alterações:\n\n"
        "1. confirmar que o projeto compila;\n"
        "2. executar os testes aplicáveis;\n"
        "3. confirmar que não existem referências de branding antigas;\n"
        "4. manter as atribuições legais obrigatórias em `NOTICE.md` e nos ficheiros de licença.\n"
        """,
        encoding="utf-8",
    )

    (ROOT / "CODE_OF_CONDUCT.md").write_text(
        """# Código de Conduta\n\n"
        "O projeto Pulse pretende manter um espaço de colaboração profissional, respeitador e inclusivo. "
        "Espera-se que todas as contribuições sejam técnicas, construtivas e respeitem os restantes participantes.\n"
        """,
        encoding="utf-8",
    )

    # Original-project naming is permitted only here as required attribution.
    (ROOT / "NOTICE.md").write_text(
        """# Avisos legais\n\n"
        "Pulse é um trabalho derivado de SimpMusic e inclui código originalmente distribuído sob a GNU GPL.\n"
        "Os direitos de autor, licenças e avisos de terceiros aplicáveis devem ser preservados nos termos da licença.\n\n"
        "A identidade, o nome, os recursos visuais, os identificadores técnicos e a documentação do produto Pulse "
        "são independentes da identidade do projeto de origem.\n"
        """,
        encoding="utf-8",
    )


def pt_pt_normalise(text: str) -> str:
    # Conservative conversion of common pt-BR product terminology to pt-PT.
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
        ("Você", "Tu"), ("você", "tu"),
        ("Usuário", "Utilizador"), ("usuário", "utilizador"),
        ("Usuários", "Utilizadores"), ("usuários", "utilizadores"),
        ("Senha", "Palavra-passe"), ("senha", "palavra-passe"),
    ]
    for old, new in pairs:
        text = text.replace(old, new)
    return text


def make_pt_pt_default() -> None:
    res = ROOT / "composeApp/src/commonMain/composeResources"
    values = res / "values"
    pt = res / "values-pt" / "strings.xml"
    default = values / "strings.xml"
    if pt.exists():
        # Prefer the existing Portuguese catalogue where translations are available.
        # Missing keys remain in the default catalogue and are refined in a later audit.
        import xml.etree.ElementTree as ET
        default_root = ET.parse(default).getroot()
        pt_root = ET.parse(pt).getroot()
        pt_map = {e.attrib.get("name"): (e.text or "") for e in pt_root.findall("string")}
        for element in default_root.findall("string"):
            name = element.attrib.get("name")
            if name in pt_map and pt_map[name]:
                element.text = pt_pt_normalise(pt_map[name])
        ET.indent(default_root, space="    ")
        ET.ElementTree(default_root).write(default, encoding="utf-8", xml_declaration=True)

    # Pulse ships one canonical UI language while the PT-PT catalogue is audited.
    for child in res.iterdir() if res.exists() else []:
        if child.is_dir() and child.name.startswith("values-"):
            shutil.rmtree(child)


def replace_all_text() -> None:
    for path in sorted(ROOT.rglob("*")):
        if not path.is_file() or ".git" in path.parts:
            continue
        if path.name in LEGAL_FILES:
            continue
        if not is_text_file(path):
            continue
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            continue
        new = replace_text(text)
        if new != text:
            path.write_text(new, encoding="utf-8")


def move_paths() -> None:
    files = [p for p in ROOT.rglob("*") if p.is_file() and ".git" not in p.parts]
    # Deepest paths first, but compute destinations against the repository root.
    for path in sorted(files, key=lambda p: len(p.parts), reverse=True):
        rel = path.relative_to(ROOT)
        if rel.name in LEGAL_FILES:
            continue
        new_rel = map_relative_path(rel)
        if new_rel == rel:
            continue
        target = ROOT / new_rel
        target.parent.mkdir(parents=True, exist_ok=True)
        if target.exists():
            target.unlink()
        path.replace(target)

    # Remove empty source directories left by package moves.
    dirs = [p for p in ROOT.rglob("*") if p.is_dir() and ".git" not in p.parts]
    for directory in sorted(dirs, key=lambda p: len(p.parts), reverse=True):
        try:
            directory.rmdir()
        except OSError:
            pass


def replace_visual_branding() -> None:
    # Remove raster launcher assets from the previous product. Min SDK is high enough
    # to use a vector launcher icon directly.
    res = ROOT / "androidApp/src/main/res"
    if res.exists():
        for mipmap in res.glob("mipmap-*"):
            if mipmap.is_dir():
                shutil.rmtree(mipmap)
        pulse_icon = res / "drawable/pulse_icon.xml"
        pulse_icon.parent.mkdir(parents=True, exist_ok=True)
        pulse_icon.write_text(
            """<?xml version="1.0" encoding="utf-8"?>\n"
            "<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">\n"
            "    <path android:fillColor="#101114" android:pathData="M0,0h108v108h-108z"/>\n"
            "    <path android:fillColor="#FFFFFF" android:pathData="M31,24h24c17,0 28,9 28,24c0,16 -11,25 -29,25h-9v15h-14zM45,37v23h9c9,0 14,-4 14,-12c0,-7 -5,-11 -14,-11z"/>\n"
            "</vector>\n""",
            encoding="utf-8",
        )

    manifest = ROOT / "androidApp/src/main/AndroidManifest.xml"
    if manifest.exists():
        text = manifest.read_text(encoding="utf-8")
        text = re.sub(r'android:icon="@[^"]+"', 'android:icon="@drawable/pulse_icon"', text)
        text = re.sub(r'\s*android:roundIcon="@[^"]+"', '', text)
        manifest.write_text(text, encoding="utf-8")

    common_drawable = ROOT / "composeApp/src/commonMain/composeResources/drawable"
    if common_drawable.exists():
        for name in ["app_icon.png", "circle_app_icon.png"]:
            p = common_drawable / name
            if p.exists():
                p.unlink()
        icon = common_drawable / "app_icon.xml"
        icon.write_text(
            """<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">\n"
            "<path android:fillColor="#101114" android:pathData="M0,0h108v108h-108z"/>\n"
            "<path android:fillColor="#FFFFFF" android:pathData="M31,24h24c17,0 28,9 28,24c0,16 -11,25 -29,25h-9v15h-14zM45,37v23h9c9,0 14,-4 14,-12c0,-7 -5,-11 -14,-11z"/>\n"
            "</vector>\n""",
            encoding="utf-8",
        )
        (common_drawable / "circle_app_icon.xml").write_text(icon.read_text(encoding="utf-8"), encoding="utf-8")

    appimage = ROOT / "composeApp/appimage"
    if appimage.exists():
        shutil.rmtree(appimage)


def disable_old_product_endpoints() -> None:
    # Any endpoint still naming the previous product is not allowed to survive.
    # Remove obsolete URL-bearing lines from project-owned text; legal files are excluded.
    forbidden_domains = ("simpmusic.org", "maxrave-dev")
    for path in sorted(ROOT.rglob("*")):
        if not path.is_file() or path.name in LEGAL_FILES or ".git" in path.parts:
            continue
        if not is_text_file(path):
            continue
        try:
            lines = path.read_text(encoding="utf-8").splitlines()
        except UnicodeDecodeError:
            continue
        if not any(any(token in line.lower() for token in forbidden_domains) for line in lines):
            continue
        cleaned = [line for line in lines if not any(token in line.lower() for token in forbidden_domains)]
        path.write_text("\n".join(cleaned) + "\n", encoding="utf-8")


def audit() -> None:
    forbidden = [
        "simpmusic", "simpmusic.org", "simpmusic://", "com.maxrave", "org.simpmusic",
        "com.simpmusic", "maxrave-dev",
    ]
    failures: list[str] = []
    for path in sorted(ROOT.rglob("*")):
        if ".git" in path.parts:
            continue
        rel = path.relative_to(ROOT).as_posix()
        if path.name not in LEGAL_FILES and any(x in rel.lower() for x in ["simpmusic", "maxrave"]):
            failures.append(f"caminho antigo: {rel}")
        if not path.is_file() or path.name in LEGAL_FILES or not is_text_file(path):
            continue
        try:
            low = path.read_text(encoding="utf-8").lower()
        except UnicodeDecodeError:
            continue
        for token in forbidden:
            if token.lower() in low:
                failures.append(f"{rel}: contém {token}")
                break
    if failures:
        print("\n".join(failures[:200]))
        raise SystemExit(f"Auditoria Pulse falhou: {len(failures)} ocorrências")


def main() -> None:
    remove_inherited_project_material()
    make_pt_pt_default()
    replace_all_text()
    move_paths()
    replace_visual_branding()
    disable_old_product_endpoints()
    rewrite_project_docs()
    audit()
    print("Rebrand Pulse concluído sem referências antigas fora dos avisos legais.")


if __name__ == "__main__":
    main()
