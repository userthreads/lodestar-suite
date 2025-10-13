# 🏗️ Architecture Overview

This document provides a detailed technical overview of Lodestar Suite's architecture, systems, and design patterns.

## 🎯 Table of Contents

1. [System Overview](#system-overview)
2. [Core Architecture](#core-architecture)
3. [Module System](#module-system)
4. [Event System](#event-system)
5. [GUI System](#gui-system)
6. [Rendering Pipeline](#rendering-pipeline)
7. [Data Persistence](#data-persistence)
8. [Network Layer](#network-layer)
9. [Security Architecture](#security-architecture)
10. [Performance Considerations](#performance-considerations)

## 🎯 System Overview

Lodestar Suite is built on a modular architecture with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Lodestar Suite                           │
├─────────────────────────────────────────────────────────────┤
│  User Interface Layer (GUI, HUD, Themes)                   │
├─────────────────────────────────────────────────────────────┤
│  Module System (Player, Render, World, Misc)               │
├─────────────────────────────────────────────────────────────┤
│  Core Systems (Config, Friends, Waypoints, Profiles)       │
├─────────────────────────────────────────────────────────────┤
│  Event System (Orbit Event Bus)                            │
├─────────────────────────────────────────────────────────────┤
│  Utility Layer (Utils, Network, Rendering)                 │
├─────────────────────────────────────────────────────────────┤
│  Minecraft Integration (Mixins, Fabric API)                │
└─────────────────────────────────────────────────────────────┘
```

## 🏗️ Core Architecture

### Main Entry Point
```java
public class MeteorClient implements ClientModInitializer {
    public static final String MOD_ID = "lodestar-suite";
    public static final IEventBus EVENT_BUS = new EventBus();
    public static final File FOLDER = FabricLoader.getInstance().getGameDir().resolve(MOD_ID).toFile();
    
    @Override
    public void onInitializeClient() {
        // Initialize core systems
        Systems.init();
        
        // Register event handlers
        EVENT_BUS.registerLambdaFactory("meteordevelopment.meteorclient", 
            (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        
        // Setup shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            OnlinePlayers.leave();
            Systems.save();
            GuiThemes.save();
        }));
    }
}
```

### System Initialization
```java
public class Systems {
    private static final List<System<?>> systems = new ArrayList<>();
    
    public static void init() {
        // Initialize core systems in order
        add(new Modules());
        Config config = new Config();
        System<?> configSystem = add(config);
        configSystem.init();
        configSystem.load();
        
        // Initialize other systems
        add(new Macros());
        add(new Friends());
        add(new Waypoints());
        add(new Profiles());
        add(new Hud());
        add(new HalloweenMode());
        add(new ChristmasMode());
    }
}
```

## 📦 Module System

### Module Architecture
Modules are the primary feature units in Lodestar Suite:

```java
public abstract class Module {
    protected final ModuleInfo<?> info;
    protected final SettingGroup settings;
    
    public Module(ModuleInfo<?> info) {
        this.info = info;
        this.settings = new SettingGroup();
    }
    
    // Lifecycle methods
    public void onActivate() {}
    public void onDeactivate() {}
    public void onTick() {}
    public void onRender(Renderer2D renderer) {}
}
```

### Module Categories
```java
public class Categories {
    public static final Category PLAYER = new Category("Player", "Player-related modules");
    public static final Category RENDER = new Category("Render", "Visual enhancement modules");
    public static final Category WORLD = new Category("World", "World-related modules");
    public static final Category MISC = new Category("Misc", "Miscellaneous modules");
    
    // Combat and Movement categories are empty (removed for fair play)
    public static final Category COMBAT = new Category("Combat", "Combat modules (removed)");
    public static final Category MOVEMENT = new Category("Movement", "Movement modules (removed)");
}
```

### Module Registration
```java
public class Modules extends System<Modules> {
    private void initPlayer() {
        add(new AutoClicker());
    }
    
    private void initRender() {
        add(new BetterTab());
        add(new BetterTooltips());
        add(new BreakIndicators());
        add(new WaypointsModule());
    }
    
    private void initWorld() {
        add(new Ambience());
    }
    
    private void initMisc() {
        add(new AutoReconnect());
        add(new BetterChat());
        add(new DiscordPresence());
        add(new ServerSpoof());
    }
}
```

## 🎪 Event System

### Orbit Event Bus
Lodestar Suite uses Orbit for event handling:

```java
public class EventBus implements IEventBus {
    private final Map<Class<?>, List<EventHandler>> handlers = new ConcurrentHashMap<>();
    
    @Override
    public <T> void subscribe(Class<T> klass, EventHandler<T> handler) {
        handlers.computeIfAbsent(klass, k -> new ArrayList<>()).add(handler);
    }
    
    @Override
    public <T> void post(T event) {
        List<EventHandler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                handler.handle(event);
            }
        }
    }
}
```

### Event Types
```java
// Tick events
public class TickEvent {
    public static class Pre extends TickEvent {}
    public static class Post extends TickEvent {}
}

// Render events
public class RenderEvent {
    public static class Pre extends RenderEvent {}
    public static class Post extends RenderEvent {}
}

// Packet events
public class PacketEvent {
    public static class Receive extends PacketEvent {
        public Packet<?> packet;
    }
    public static class Send extends PacketEvent {
        public Packet<?> packet;
    }
}
```

### Event Handling Example
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
    
    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            // Handle time update packet
        }
    }
}
```

## 🖥️ GUI System

### GUI Architecture
```java
public abstract class WindowScreen extends Screen {
    protected final GuiTheme theme;
    protected final String title;
    protected final List<WWidget> widgets = new ArrayList<>();
    
    public WindowScreen(GuiTheme theme, String title) {
        super(Text.literal(title));
        this.theme = theme;
        this.title = title;
    }
    
    @Override
    protected void init() {
        initWidgets();
        super.init();
    }
    
    protected abstract void initWidgets();
}
```

### Theme System
```java
public abstract class GuiTheme {
    protected final String name;
    
    public GuiTheme(String name) {
        this.name = name;
    }
    
    // Widget creation methods
    public abstract WWindow window(WWindow.WindowType type);
    public abstract WButton button(String text);
    public abstract WLabel label(String text);
    public abstract WSlider slider(double value, double min, double max);
    // ... more widget types
}
```

### Widget System
```java
public abstract class WWidget {
    protected double x, y;
    protected double width, height;
    protected boolean visible = true;
    protected boolean active = true;
    
    public abstract void render(Renderer2D renderer, double mouseX, double mouseY, double delta);
    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);
    public abstract boolean keyPressed(int key, int scanCode, int modifiers);
}
```

## 🎨 Rendering Pipeline

### Renderer2D
```java
public class Renderer2D {
    private final MatrixStack matrices;
    private final BufferBuilder buffer;
    
    public void begin() {
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
    }
    
    public void end() {
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
    
    public void quad(double x1, double y1, double x2, double y2, Color color) {
        // Render quad with specified coordinates and color
    }
    
    public void text(String text, double x, double y, Color color) {
        // Render text at specified position
    }
}
```

### HUD Rendering
```java
public class HudRenderer {
    private final Renderer2D renderer;
    
    public void render(HudElement element) {
        element.render(renderer);
    }
    
    public void renderAll() {
        for (HudElement element : Hud.INSTANCE.elements) {
            if (element.isActive()) {
                render(element);
            }
        }
    }
}
```

## 💾 Data Persistence

### System Base Class
```java
public abstract class System<T> implements ISerializable<T> {
    protected abstract NbtCompound toTag();
    protected abstract T fromTag(NbtCompound tag);
    
    public void save(File folder) {
        try {
            NbtCompound tag = toTag();
            File tempFile = new File(folder, getName() + ".tmp");
            File file = new File(folder, getName() + ".nbt");
            
            // Atomic write with backup
            NbtIo.write(tag, tempFile.toPath());
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            MeteorClient.LOG.error("Failed to save system " + getName(), e);
        }
    }
    
    public void load(File folder) {
        File file = new File(folder, getName() + ".nbt");
        if (!file.exists()) return;
        
        try {
            NbtCompound tag = NbtIo.read(file.toPath());
            fromTag(tag);
        } catch (Exception e) {
            MeteorClient.LOG.error("Failed to load system " + getName(), e);
            // Create backup of corrupted file
            try {
                Files.copy(file.toPath(), new File(folder, getName() + ".corrupted").toPath());
            } catch (IOException ignored) {}
        }
    }
}
```

### Settings Persistence
```java
public class Setting<T> {
    protected T value;
    protected final T defaultValue;
    
    public NbtCompound save(NbtCompound tag) {
        // Save setting value to NBT
        return tag;
    }
    
    public T load(NbtCompound tag) {
        // Load setting value from NBT
        return value;
    }
}
```

## 🌐 Network Layer

### HTTP Client
```java
public class Http {
    public static class Request {
        private final HttpRequest.Builder builder;
        private final Method method;
        
        public static Request get(String url) {
            return new Request(Method.GET, url);
        }
        
        public static Request post(String url) {
            return new Request(Method.POST, url);
        }
        
        public Response send() {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = builder.build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return new Response(response);
            } catch (Exception e) {
                return new FailedHttpResponse(e);
            }
        }
    }
}
```

### Online Players Tracking
```java
public class OnlinePlayers {
    private static final Timer timer = new Timer();
    
    public static void init() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 5 * 60 * 1000); // Every 5 minutes
    }
    
    public static void update() {
        MeteorExecutor.execute(() -> {
            Http.post("https://meteorclient.com/api/online/ping")
                .ignoreExceptions()
                .send();
        });
    }
}
```

## 🔒 Security Architecture

### Removed Security Risks
Lodestar Suite has removed several security-sensitive components:

```java
// REMOVED: Proxy system for security reasons
// REMOVED: Account system for security reasons
// REMOVED: Baritone pathfinding for fair play compliance
```

### Input Validation
```java
public class Http {
    private Request(Method method, String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
                throw new IllegalArgumentException("Invalid URL scheme: " + scheme);
            }
            if (scheme.equals("http")) {
                System.err.println("Warning: Using HTTP instead of HTTPS for URL: " + url);
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }
}
```

### Safe File Operations
```java
public class System<T> {
    public void save(File folder) {
        // Atomic file operations with backup
        File tempFile = new File(folder, getName() + ".tmp");
        File file = new File(folder, getName() + ".nbt");
        
        NbtIo.write(tag, tempFile.toPath());
        Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
```

## ⚡ Performance Considerations

### Event System Optimization
```java
public class EventBus {
    private final Map<Class<?>, List<EventHandler>> handlers = new ConcurrentHashMap<>();
    
    // Use ConcurrentHashMap for thread-safe access
    // Pre-compute handler lists to avoid repeated lookups
}
```

### Rendering Optimization
```java
public class Renderer2D {
    private final BufferBuilder buffer;
    
    // Batch rendering operations
    public void begin() {
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
    }
    
    public void end() {
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
```

### Memory Management
```java
public class TPSGraphHud extends HudElement {
    private final List<Float> tpsHistory = new ArrayList<>();
    private final List<Long> timeHistory = new ArrayList<>();
    
    @Override
    public void tick(HudRenderer renderer) {
        // Limit history size to prevent memory leaks
        while (tpsHistory.size() > maxDataPoints.get()) {
            tpsHistory.remove(0);
            timeHistory.remove(0);
        }
    }
}
```

## 🔧 Build System

### Gradle Configuration
```kotlin
plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
}

// Custom JAR naming with random suffix
fun generateRandomSuffix(): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..5).map { chars.random() }.joinToString("")
}

val versionSuffix = generateRandomSuffix()
val customArchivesName = "${baseName}-${minecraftVersion}-${versionSuffix}"
```

### Dependency Management
```kotlin
dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    
    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fapi_version")}")
    
    // Dependencies
    implementation("meteordevelopment:orbit:${project.property("orbit_version")}")
    implementation("meteordevelopment:starscript:${project.property("starscript_version")}")
    implementation("meteordevelopment:discord-ipc:${project.property("discordipc_version")}")
}
```

## 🧪 Testing Architecture

### Unit Testing
```java
@Test
public void testModuleActivation() {
    ExampleModule module = new ExampleModule();
    assertFalse(module.isActive());
    
    module.toggle();
    assertTrue(module.isActive());
}
```

### Integration Testing
```java
@Test
public void testSystemInitialization() {
    Systems.init();
    assertNotNull(Systems.get(Modules.class));
    assertNotNull(Systems.get(Config.class));
}
```

## 📊 Monitoring & Debugging

### Logging System
```java
public class MeteorClient {
    public static final Logger LOG = LoggerFactory.getLogger(NAME);
    
    // Use appropriate log levels
    LOG.info("System initialized");
    LOG.warn("Deprecated feature used");
    LOG.error("Error occurred", exception);
}
```

### Performance Monitoring
```java
public class TPSGraphHud extends HudElement {
    private float minTPS = Float.MAX_VALUE;
    private float maxTPS = 0.0f;
    private float totalTPS = 0.0f;
    private int dataPointCount = 0;
    
    // Track performance metrics
    private void updateStatistics(float currentTPS) {
        minTPS = Math.min(minTPS, currentTPS);
        maxTPS = Math.max(maxTPS, currentTPS);
        totalTPS += currentTPS;
        dataPointCount++;
    }
}
```

---

This architecture provides a solid foundation for Lodestar Suite's functionality while maintaining clean separation of concerns, good performance, and security best practices.
