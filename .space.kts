/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build and run tests") {
   container("openjdk:11"){
      shellScript {
         content = """
            sudo apt install -y libappindicator1 fonts-liberation
            wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
            sudo dpkg -i google-chrome*.deb
         """.trimIndent()
      }
      kotlinScript { api ->
         api.gradlew("build")
      }

   }
}
