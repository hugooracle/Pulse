# Building Pulse

After the initial rebrand lands, clone with submodules:

```powershell
git clone --recurse-submodules https://github.com/hugooracle/Pulse.git
cd Pulse
git checkout dev
git submodule update --init --recursive
```

Open the repository root in Android Studio and build the `androidApp` debug APK.
