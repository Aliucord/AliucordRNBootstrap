# AliucordRN Bootstrap

Very early Aliucord loader for DiscordRn.

## Instructions

- Decompile base.apk with apktool and apply [the smali patch](smali-patch.diff). Will be changed later, just smali patch for simplicity for now
  (modified dex is classes4.dex)
- Move dex files by one to make room for a new classes.dex
- Add Bootstrap/build/Injector.dex as classes.dex

