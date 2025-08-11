plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.postgresql:postgresql:42.7.5")

    // For logging purposes
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // Domain model for unit testing
    testImplementation(project(":chatDomainModel"))
    testImplementation(project(":carDomainModel"))

    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5439/db?user=dbuser&password=changeit")
    environment("CAR_DB_URL", "jdbc:postgresql://localhost:5440/cardb?user=dbuser&password=changeit")
    dependsOn(":jdbcRepoLib:dbTestsWait", ":jdbcRepoLib:carDbTestsWait")
    finalizedBy(":jdbcRepoLib:dbTestsDown", ":jdbcRepoLib:carDbTestsDown")
}

/**
 * DB related tasks
 * - To run `psql` inside the container, do
 *      docker exec -ti chat-db psql -d db -U dbuser -W
 *   and provide it with the same password as define on `tests/Dockerfile-chat-db`
 */

val dockerDir: Directory = project.layout.projectDirectory.dir("src/test/docker/")
val dockerComposePath = dockerDir.file("docker-compose.yml").toString()

task<Exec>("dbTestsUp") {
    commandLine(
        "docker",
        "compose",
        "-p",
        "jdbc-repo",
        "-f",
        dockerComposePath,
        "up",
        "-d",
        "--build",
        "--force-recreate",
        "chat-db",
    )
}

task<Exec>("dbTestsWait") {
    commandLine("docker", "exec", "chat-db", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
    commandLine("docker", "compose", "-p", "jdbc-repo", "-f", dockerComposePath, "down", "chat-db")
}

task<Exec>("carDbTestsUp") {
    commandLine(
        "docker",
        "compose",
        "-p",
        "jdbc-repo-car",
        "-f",
        dockerComposePath,
        "up",
        "-d",
        "--build",
        "--force-recreate",
        "car-db",
    )
}

task<Exec>("carDbTestsWait") {
    commandLine("docker", "exec", "car-db", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("carDbTestsUp")
}

task<Exec>("carDbTestsDown") {
    commandLine("docker", "compose", "-p", "jdbc-repo-car", "-f", dockerComposePath, "down", "car-db")
}
