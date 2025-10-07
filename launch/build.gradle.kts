plugins {
    id("java")
}

group = "waythread"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<JavaCompile> {
        options.release = 8
        options.compilerArgs.add("-Xlint:-options") // Suppress Java 8 deprecation warnings
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:all")
    }
}
