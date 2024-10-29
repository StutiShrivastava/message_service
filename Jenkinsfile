pipeline {
    agent any
    environment {
        PATH = "${env.PATH};C:\\Users\\a5143522\\Maven\\apache-maven-3.9.6\\bin"
        JAVA_HOME = "C:\\Users\\a5143522\\Java\\openjdk-17.0.12"
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/StutiShrivastava/message_service.git'
            }
        }
        stage('Build & Deploy') {
             steps {
                  // Build & Deploy
                  echo 'Executing Build...'
                  bat "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
                  echo 'Build & Deployment Completed...'
             }
        }
        stage('Test') {
              steps {
                   script {
                        // Executing Unit Test
                        echo 'Executing Test...'
                        bat "mvn test"
                   }
              }
        }
    }
}
