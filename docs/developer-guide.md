# 🔧 Developer Guide

Welcome to the Lodestar Suite Developer Guide! This guide will help you understand the codebase, contribute to the project, and develop new features.

## 🎯 Table of Contents

1. [Getting Started](#getting-started)
2. [Project Structure](#project-structure)
3. [Development Setup](#development-setup)
4. [Architecture Overview](#architecture-overview)
5. [Contributing Guidelines](#contributing-guidelines)
6. [Code Standards](#code-standards)
7. [Testing](#testing)
8. [Building & Deployment](#building--deployment)

## 🚀 Getting Started

### Prerequisites
- **Java 21** or later
- **Git** for version control
- **IntelliJ IDEA** (recommended) or **Eclipse**
- **Gradle** (included in the project)

### Quick Start
```bash
# Clone the repository
git clone https://github.com/waythread/lodestar-suite.git
cd lodestar-suite

# Build the project
./gradlew build

# Run in development
./gradlew runClient
```

## 📁 Project Structure

```
lodestar-suite/
├── src/main/java/meteordevelopment/meteorclient/
│   ├── commands/          # Command system
│   ├── gui/              # User interface
│   ├── mixin/            # Minecraft modifications
│   ├── systems/          # Core systems
│   ├── utils/            # Utility classes
│   └── MeteorClient.java # Main entry point
├── src/main/resources/
│   ├── assets/           # Textures, fonts, shaders
│   ├── fabric.mod.json   # Mod metadata
│   └── *.mixins.json     # Mixin configurations
├── launch/               # Launcher subproject
├── docs/                 # Documentation
└── build.gradle.kts      # Build configuration
```

### Key Directories

#### `systems/`
Core systems that manage different aspects of the mod:
- **modules/**: Individual feature modules
- **hud/**: HUD elements and rendering
- **config/**: Configuration management
- **friends/**: Friend system
- **waypoints/**: Waypoint management

#### `gui/`
User interface components:
- **screens/**: Main menu screens
- **themes/**: Visual themes
- **widgets/**: UI components

#### `mixin/`
Minecraft modifications using Mixin:
- **Client-side modifications**
- **Rendering enhancements**
- **Game mechanics changes**

## 🛠️ Development Setup

### 1. Clone and Setup
```bash
git clone https://github.com/waythread/lodestar-suite.git
cd lodestar-suite
```

### 2. IDE Setup (IntelliJ IDEA)
1. Open IntelliJ IDEA
2. Select "Open or Import"
3. Choose the `lodestar-suite` folder
4. Wait for Gradle sync to complete
5. Set up run configurations:
   - **Main class**: `net.fabricmc.devlaunchinjector.Main`
   - **VM options**: `-Dfabric.dli.config=C:\Users\YourName\.gradle\caches\fabric-loom\minecraft\1.21.8\minecraft-1.21.8-client.jar -Dfabric.dli.env=client -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient`

### 3. Build Configuration
```bash
# Clean build
./gradlew clean build

# Run client for testing
./gradlew runClient

# Generate sources
./gradlew genSources
```

## 🏗️ Architecture Overview

### Core Systems

#### Module System
Modules are the main feature units in Lodestar Suite:

```java
public class ExampleModule extends Module {
    public static final ModuleInfo<ExampleModule> INFO = new ModuleInfo<>(
        Categories.MISC, "example", "Example module description", ExampleModule::new
    );
    
    public ExampleModule() {
        super(INFO);
    }
    
    @Override
    public void onActivate() {
        // Module activated
    }
    
    @Override
    public void onDeactivate() {
        // Module deactivated
    }
}
```

#### Event System
Lodestar Suite uses Orbit for event handling:

```java
public class ExampleModule extends Module {
    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Handle tick event
    }
    
    @EventHandler
    private void onRender(RenderEvent event) {
        // Handle render event
    }
}
```

#### Settings System
Modules can have configurable settings:

```java
public class ExampleModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable this feature")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in milliseconds")
        .defaultValue(1000)
        .min(100)
        .max(5000)
        .build()
    );
}
```

### GUI System

#### Screen Creation
```java
public class ExampleScreen extends WindowScreen {
    public ExampleScreen(GuiTheme theme) {
        super(theme, "Example Screen");
    }
    
    @Override
    public void initWidgets() {
        add(theme.button("Click Me")).expandX().widget().action = () -> {
            // Button action
        };
    }
}
```

#### Theme System
```java
public class ExampleTheme extends GuiTheme {
    public ExampleTheme() {
        super("Example Theme");
    }
    
    @Override
    public WWindow window(WWindow.WindowType type) {
        return w(new ExampleWindow(type));
    }
}
```

### Mixin System

#### Creating Mixins
```java
@Mixin(ExampleClass.class)
public class ExampleClassMixin {
    @Inject(method = "exampleMethod", at = @At("HEAD"))
    private void onExampleMethod(CallbackInfo info) {
        // Inject code at the beginning of the method
    }
    
    @ModifyVariable(method = "exampleMethod", at = @At("STORE"))
    private int modifyExampleVariable(int value) {
        // Modify the variable value
        return value * 2;
    }
}
```

## 📝 Contributing Guidelines

### 1. Fork and Clone
```bash
# Fork the repository on GitHub
# Then clone your fork
git clone https://github.com/your-username/lodestar-suite.git
cd lodestar-suite
```

### 2. Create Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### 3. Make Changes
- Follow the code standards
- Add tests if applicable
- Update documentation
- Test your changes thoroughly

### 4. Commit Changes
```bash
git add .
git commit -m "feat: add your feature description"
```

### 5. Push and Create PR
```bash
git push origin feature/your-feature-name
# Create Pull Request on GitHub
```

### Pull Request Guidelines
- **Clear title**: Describe what the PR does
- **Description**: Explain the changes and why
- **Testing**: Describe how you tested the changes
- **Screenshots**: Include screenshots for UI changes
- **Breaking changes**: Note any breaking changes

## 📋 Code Standards

### Java Code Style
- **Indentation**: 4 spaces
- **Line length**: 120 characters max
- **Naming**: camelCase for variables, PascalCase for classes
- **Comments**: Javadoc for public methods

### Example Code Style
```java
/**
 * Example class demonstrating code style
 */
public class ExampleClass {
    private static final String CONSTANT = "value";
    private final String instanceVariable;
    
    public ExampleClass(String parameter) {
        this.instanceVariable = parameter;
    }
    
    /**
     * Example method with proper documentation
     * @param input The input parameter
     * @return The processed result
     */
    public String processInput(String input) {
        if (input == null) {
            return "default";
        }
        
        return input.toUpperCase();
    }
}
```

### File Organization
- **One class per file**
- **Package structure**: Follow existing patterns
- **Imports**: Organize and remove unused imports
- **Static imports**: Use sparingly

### Documentation
- **Javadoc**: For all public methods and classes
- **Inline comments**: For complex logic
- **README updates**: For new features
- **Changelog**: For user-facing changes

## 🧪 Testing

### Unit Testing
```java
@Test
public void testExampleMethod() {
    ExampleClass example = new ExampleClass("test");
    String result = example.processInput("hello");
    assertEquals("HELLO", result);
}
```

### Integration Testing
- Test modules in-game
- Verify GUI functionality
- Check event handling
- Test with different Minecraft versions

### Manual Testing Checklist
- [ ] Module enables/disables correctly
- [ ] Settings save and load properly
- [ ] GUI elements work as expected
- [ ] No crashes or errors
- [ ] Performance is acceptable

## 🏗️ Building & Deployment

### Local Build
```bash
# Clean build
./gradlew clean build

# Build without tests
./gradlew build -x test

# Build with specific version
./gradlew build -Pversion=1.0.0
```

### Release Build
```bash
# Create release build
./gradlew clean build --no-configuration-cache

# JAR will be in build/libs/
# Format: lodestar-suite-{version}-{suffix}.jar
```

### Deployment Process
1. **Update version** in `gradle.properties`
2. **Build release** with `./gradlew clean build`
3. **Test thoroughly** in different environments
4. **Create GitHub release** with JAR file
5. **Update documentation** if needed

## 🔍 Debugging

### Common Issues
- **Mixin conflicts**: Check for conflicting mixins
- **Class loading**: Verify classpath and dependencies
- **Event handling**: Check event registration
- **GUI rendering**: Verify OpenGL context

### Debug Tools
- **IntelliJ debugger**: Set breakpoints and step through code
- **Minecraft logs**: Check `logs/latest.log` for errors
- **Fabric logs**: Look for mixin and mod loading issues
- **Performance profiler**: Use JProfiler or similar tools

### Logging
```java
import meteordevelopment.meteorclient.MeteorClient;

// Use the main logger
MeteorClient.LOG.info("Info message");
MeteorClient.LOG.warn("Warning message");
MeteorClient.LOG.error("Error message", exception);
```

## 📚 Additional Resources

### Documentation
- **[Architecture Overview](architecture.md)**: Detailed system design
- **[API Reference](api-reference.md)**: Code documentation
- **[Module Development](module-development.md)**: Creating new modules

### External Resources
- **[Fabric Documentation](https://fabricmc.net/wiki/)**: Fabric modding guide
- **[Mixin Documentation](https://github.com/SpongePowered/Mixin)**: Mixin system
- **[Orbit Documentation](https://github.com/orbit/orbit)**: Event system

### Community
- **GitHub Discussions**: Ask questions and get help
- **Issues**: Report bugs and request features
- **Discord**: Join the community (if available)

---

**Ready to contribute?** Check out our [Contributing Guidelines](#contributing-guidelines) and start coding!
