plugins {
    application
    kotlin("jvm") version "2.0.21"
    id("io.freefair.lombok") version "8.11"
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.joaovittor"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("send.email.function.Handler")
}

repositories {
    mavenCentral()
}

// Dependencies versions
val awsSdkVersion = "2.16.29"
val awsSesVersion = "2.16.23"
val glassfishElApiVersion = "3.0.0"
val awsLambdaCoreVersion = "1.2.2"
val awsLambdaEventsVersion = "3.11.1"
val hibernateValidatorVersion = "6.2.0.Final"

dependencies {
    testImplementation(kotlin("test"))
    implementation("software.amazon.awssdk:s3:$awsSdkVersion")
    implementation("software.amazon.awssdk:ses:$awsSesVersion")
    implementation("org.glassfish:javax.el:$glassfishElApiVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.amazonaws:aws-lambda-java-core:$awsLambdaCoreVersion")
    implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaEventsVersion")
    implementation("org.hibernate.validator:hibernate-validator:$hibernateValidatorVersion")
    implementation("org.hibernate.validator:hibernate-validator-annotation-processor:$hibernateValidatorVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}
