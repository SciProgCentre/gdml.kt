job("Build") {
    container("openjdk:11.0.10-jdk-buster") {
        shellScript {
            interpreter = "/bin/bash"

            content = """
                apt update
                apt install -y libncurses5
                ./gradlew --no-daemon build
            """.trimIndent()
        }
    }
}