plugins {
    alias(libs.plugins.protobuf)
}

dependencies {
    implementation(libs.protobuf.javalite)
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:4.26.1" }
    generateProtoTasks { all().forEach { it.plugins { create("java") { option("lite") } } } }
}