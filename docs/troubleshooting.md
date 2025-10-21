# 🛠️ Troubleshooting Guide

This guide helps you solve common problems with Lodestar Suite. If you can't find your issue here, check our [FAQ](faq.md) or [contact us](https://github.com/userthreads/lodestar-suite/issues).

## 🎯 Table of Contents

1. [Installation Issues](#installation-issues)
2. [Launch Problems](#launch-problems)
3. [Module Issues](#module-issues)
4. [GUI Problems](#gui-problems)
5. [Performance Issues](#performance-issues)
6. [Compatibility Problems](#compatibility-problems)
7. [Data Loss Issues](#data-loss-issues)
8. [Network Issues](#network-issues)
9. [Advanced Troubleshooting](#advanced-troubleshooting)

## 📥 Installation Issues

### Problem: "Lodestar Suite doesn't appear in Minecraft"

**Symptoms:**
- Minecraft starts normally but no Lodestar Suite menu
- No "Lodestar Suite" text in title screen credits
- Right Shift doesn't open any menu

**Solutions:**

1. **Check Fabric Profile**
   ```
   - Make sure you're using the Fabric profile in Minecraft launcher
   - Not the default Minecraft profile
   - Fabric profile should appear after installing Fabric Loader
   ```

2. **Verify JAR File Location**
   ```
   Windows: %appdata%\.minecraft\mods\
   Mac: ~/Library/Application Support/minecraft/mods/
   Linux: ~/.minecraft/mods/
   
   - File should be named: lodestar-suite-1.21.8-*.jar
   - Not in a subfolder
   - Not in the main Minecraft folder
   ```

3. **Check Java Version**
   ```
   - Lodestar Suite requires Java 21 or later
   - Check: java -version
   - Update Java if needed
   ```

4. **Verify File Integrity**
   ```
   - Re-download from official GitHub releases
   - Check file size (should be ~9.6MB)
   - Don't use files from unofficial sources
   ```

### Problem: "Game crashes on startup"

**Symptoms:**
- Minecraft crashes immediately after starting
- Error messages in launcher
- Black screen or freeze

**Solutions:**

1. **Check Minecraft Logs**
   ```
   Location: .minecraft/logs/latest.log
   Look for error messages related to:
   - Fabric Loader
   - Lodestar Suite
   - Java version
   - Missing dependencies
   ```

2. **Remove Other Mods**
   ```
   - Temporarily move other mods out of mods folder
   - Test if Lodestar Suite works alone
   - Add mods back one by one to find conflicts
   ```

3. **Update Dependencies**
   ```
   - Update Fabric Loader to latest version
   - Update Fabric API if using other mods
   - Check for conflicting mod versions
   ```

4. **Java Issues**
   ```
   - Ensure Java 21+ is installed
   - Check JAVA_HOME environment variable
   - Try different Java version if available
   ```

## 🚀 Launch Problems

### Problem: "Menu doesn't open"

**Symptoms:**
- Right Shift doesn't work
- No response when pressing menu key
- Menu keybind seems broken

**Solutions:**

1. **Check Keybind Settings**
   ```
   - Go to Minecraft Settings → Controls
   - Look for "Lodestar Suite Menu" keybind
   - Change to a different key (like F1)
   - Test the new keybind
   ```

2. **Reset Keybinds**
   ```
   - Delete .minecraft/lodestar-suite/ folder
   - Restart Minecraft
   - Default keybind (Right Shift) should work
   ```

3. **Key Conflicts**
   ```
   - Check for conflicting keybinds
   - Other mods might use same key
   - Try a unique key like F12 or Insert
   ```

4. **Restart Minecraft**
   ```
   - Close Minecraft completely
   - Restart the game
   - Try opening menu again
   ```

### Problem: "Menu opens but is empty/blank"

**Symptoms:**
- Menu opens but shows no content
- Blank screen or error messages
- Modules don't appear

**Solutions:**

1. **Check Module Loading**
   ```
   - Look for error messages in Minecraft logs
   - Check if modules are being loaded properly
   - Verify no corrupted files
   ```

2. **Reset Configuration**
   ```
   - Delete .minecraft/lodestar-suite/ folder
   - Restart Minecraft
   - Modules should appear with default settings
   ```

3. **Update Lodestar Suite**
   ```
   - Download latest version
   - Replace old JAR file
   - Check for compatibility issues
   ```

## 📦 Module Issues

### Problem: "Module doesn't work as expected"

**Symptoms:**
- Module is enabled but doesn't function
- Settings don't take effect
- Module behaves differently than described

**Solutions:**

1. **Check Module Settings**
   ```
   - Open module settings (gear icon)
   - Verify all settings are configured correctly
   - Check for conflicting settings
   - Reset to defaults if needed
   ```

2. **Module Dependencies**
   ```
   - Some modules require other modules
   - Check module descriptions for requirements
   - Enable required modules first
   ```

3. **Server Compatibility**
   ```
   - Some modules might not work on all servers
   - Test on singleplayer first
   - Check server rules and policies
   ```

4. **Update Module**
   ```
   - Update to latest Lodestar Suite version
   - Check for module-specific bug fixes
   - Report issues if problem persists
   ```

### Problem: "Module causes crashes"

**Symptoms:**
- Game crashes when enabling specific module
- Error messages related to module
- Inconsistent crashes

**Solutions:**

1. **Disable Problematic Module**
   ```
   - Disable the module causing issues
   - Check if crashes stop
   - Report the issue with module name
   ```

2. **Check Module Settings**
   ```
   - Reset module to default settings
   - Check for invalid configuration values
   - Try different setting combinations
   ```

3. **Update and Report**
   ```
   - Update to latest version
   - If problem persists, report with:
     - Module name
     - Error messages
     - Steps to reproduce
   ```

## 🖥️ GUI Problems

### Problem: "HUD elements don't appear"

**Symptoms:**
- Added HUD elements but they're not visible
- Elements appear but in wrong position
- HUD elements flicker or disappear

**Solutions:**

1. **Check HUD Visibility**
   ```
   - Open HUD menu in Lodestar Suite
   - Verify elements are enabled (green toggle)
   - Check visibility settings
   - Make sure elements aren't off-screen
   ```

2. **HUD Position Issues**
   ```
   - Use HUD editor (default key: H)
   - Drag elements to correct position
   - Check if elements are behind other UI
   - Adjust scale if elements are too small
   ```

3. **HUD Rendering Issues**
   ```
   - Check graphics settings
   - Update graphics drivers
   - Try different render distance
   - Disable other visual mods temporarily
   ```

### Problem: "Theme doesn't apply"

**Symptoms:**
- Selected theme doesn't change appearance
- Theme changes partially
- Theme reverts to default

**Solutions:**

1. **Apply Theme Correctly**
   ```
   - Go to Settings → Themes
   - Click on desired theme
   - Wait for theme to load
   - Restart Minecraft if needed
   ```

2. **Theme Compatibility**
   ```
   - Some themes might not be fully compatible
   - Try different themes
   - Check for theme-specific issues
   ```

3. **Reset Theme**
   ```
   - Delete .minecraft/lodestar-suite/ folder
   - Restart Minecraft
   - Default theme should apply
   ```

## ⚡ Performance Issues

### Problem: "Game runs slowly with Lodestar Suite"

**Symptoms:**
- Lower FPS than without mod
- Stuttering or lag
- Slow menu response

**Solutions:**

1. **Disable Unused Modules**
   ```
   - Only enable modules you actually use
   - Disable heavy modules like TPS Graph if not needed
   - Check module descriptions for performance impact
   ```

2. **Reduce HUD Elements**
   ```
   - Too many HUD elements can impact performance
   - Remove unnecessary HUD elements
   - Use simpler HUD configurations
   ```

3. **Graphics Settings**
   ```
   - Lower render distance
   - Reduce graphics quality
   - Disable fancy graphics options
   - Update graphics drivers
   ```

4. **Memory Issues**
   ```
   - Allocate more RAM to Minecraft
   - Close other applications
   - Check for memory leaks
   ```

### Problem: "TPS Graph shows incorrect values"

**Symptoms:**
- TPS values seem wrong
- Graph doesn't update
- Statistics are inaccurate

**Solutions:**

1. **Check Server Connection**
   ```
   - TPS Graph only works on multiplayer servers
   - Singleplayer doesn't show meaningful TPS
   - Make sure you're connected to a server
   ```

2. **Reset TPS Graph**
   ```
   - Disable and re-enable TPS Graph module
   - Clear graph data in settings
   - Wait for new data to accumulate
   ```

3. **Server Compatibility**
   ```
   - Some servers might not send TPS data
   - Check if server supports TPS monitoring
   - Try on different servers
   ```

## 🔧 Compatibility Problems

### Problem: "Conflicts with other mods"

**Symptoms:**
- Game crashes with specific mod combinations
- Features don't work together
- Performance issues with multiple mods

**Solutions:**

1. **Identify Conflicting Mods**
   ```
   - Remove all mods except Lodestar Suite
   - Add mods back one by one
   - Test after each addition
   - Identify the conflicting mod
   ```

2. **Mod Load Order**
   ```
   - Some mods need to load in specific order
   - Try different mod combinations
   - Check mod documentation for compatibility
   ```

3. **Version Compatibility**
   ```
   - Ensure all mods are for same Minecraft version
   - Update all mods to latest versions
   - Check for known compatibility issues
   ```

### Problem: "Server compatibility issues"

**Symptoms:**
- Kicked from server
- Features don't work on server
- Server shows warnings

**Solutions:**

1. **Check Server Rules**
   ```
   - Read server rules carefully
   - Some servers don't allow any mods
   - Contact server staff if unsure
   ```

2. **Disable Problematic Features**
   ```
   - Disable modules that might trigger anti-cheat
   - Use only quality-of-life features
   - Test on different servers
   ```

3. **Server-Specific Issues**
   ```
   - Some servers have custom anti-cheat
   - Try on vanilla-compatible servers
   - Report server-specific issues
   ```

## 💾 Data Loss Issues

### Problem: "Settings and configurations lost"

**Symptoms:**
- Custom settings reset to defaults
- Waypoints disappear
- Profiles are lost

**Solutions:**

1. **Check File Locations**
   ```
   - Settings are stored in .minecraft/lodestar-suite/
   - Make sure folder wasn't deleted
   - Check file permissions
   ```

2. **Backup and Restore**
   ```
   - Always backup your configurations
   - Copy .minecraft/lodestar-suite/ folder
   - Restore from backup if needed
   ```

3. **Prevent Future Loss**
   ```
   - Don't delete .minecraft/lodestar-suite/ folder
   - Use profiles to save different configurations
   - Export important settings
   ```

### Problem: "Waypoints disappear"

**Symptoms:**
- Created waypoints are gone
- Waypoint file is corrupted
- Can't load waypoints

**Solutions:**

1. **Check Waypoint File**
   ```
   - Location: .minecraft/lodestar-suite/waypoints.nbt
   - Make sure file exists and isn't corrupted
   - Check file permissions
   ```

2. **Restore Waypoints**
   ```
   - If you have a backup, restore it
   - Recreate important waypoints
   - Use waypoint export/import feature
   ```

3. **Prevent Future Loss**
   ```
   - Regularly backup waypoint files
   - Use waypoint export feature
   - Don't manually edit waypoint files
   ```

## 🌐 Network Issues

### Problem: "Discord Presence doesn't work"

**Symptoms:**
- Discord doesn't show Minecraft activity
- Presence appears but is incorrect
- Presence doesn't update

**Solutions:**

1. **Check Discord Integration**
   ```
   - Make sure Discord is running
   - Check if Discord Rich Presence is enabled
   - Restart Discord if needed
   ```

2. **Lodestar Suite Settings**
   ```
   - Enable Discord Presence module
   - Check module settings
   - Verify Discord application ID
   ```

3. **Network Issues**
   ```
   - Check internet connection
   - Try disabling firewall temporarily
   - Check for proxy/VPN issues
   ```

### Problem: "Online player count doesn't update"

**Symptoms:**
- Player count is always 0
- Count doesn't change
- Network errors in logs

**Solutions:**

1. **Check Network Connection**
   ```
   - Ensure stable internet connection
   - Check firewall settings
   - Try different network if available
   ```

2. **Server Issues**
   ```
   - Online player tracking might be down
   - Check GitHub for service status
   - This is a non-critical feature
   ```

## 🔍 Advanced Troubleshooting

### Problem: "Complex issues not covered above"

**Symptoms:**
- Multiple problems at once
- Unusual error messages
- Intermittent issues

**Solutions:**

1. **Gather Information**
   ```
   - Minecraft version
   - Lodestar Suite version
   - Other mods installed
   - Error messages from logs
   - Steps to reproduce issue
   ```

2. **Clean Installation**
   ```
   - Remove all mods
   - Delete .minecraft/lodestar-suite/ folder
   - Fresh install of Lodestar Suite
   - Test basic functionality
   ```

3. **System Information**
   ```
   - Operating system version
   - Java version
   - Graphics card and drivers
   - Available RAM
   ```

### Debugging Steps

1. **Enable Debug Logging**
   ```
   - Check Minecraft logs for detailed errors
   - Look for Lodestar Suite specific messages
   - Note timestamps of issues
   ```

2. **Isolate the Problem**
   ```
   - Test with minimal configuration
   - Disable all modules except one
   - Test on different servers/worlds
   ```

3. **Report Issues**
   ```
   - Use GitHub Issues for bug reports
   - Include all gathered information
   - Provide steps to reproduce
   - Attach relevant log files
   ```

## 📞 Getting Help

### Resources
- **[FAQ](faq.md)**: Common questions and answers
- **[GitHub Issues](https://github.com/userthreads/lodestar-suite/issues)**: Report bugs
- **[Discussions](https://github.com/userthreads/lodestar-suite/discussions)**: Ask questions
- **[Wiki](https://github.com/userthreads/lodestar-suite/wiki)**: Additional documentation

### When Reporting Issues
Include this information:
- **Minecraft version**: 1.21.8
- **Lodestar Suite version**: From JAR filename
- **Other mods**: List of installed mods
- **Error messages**: From Minecraft logs
- **Steps to reproduce**: What you did before the issue
- **Expected behavior**: What should have happened
- **Actual behavior**: What actually happened

### Community Help
- **GitHub Discussions**: Ask questions and get help from community
- **Issue Reports**: Report bugs and request features
- **Wiki Contributions**: Help improve documentation

---

**Still need help?** Check our [FAQ](faq.md) or [contact us](https://github.com/userthreads/lodestar-suite/issues) with detailed information about your issue!
