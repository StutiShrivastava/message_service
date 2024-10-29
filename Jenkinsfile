pipeline {
    agent any
    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yml'
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/StutiShrivastava/message_service.git'
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
        stage('Build & Deploy') {
                    steps {
                        // Build & Deploy
                        echo 'Executing Build...'
                        bat "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
                        echo 'Build & Deployment Completed...'
                    }
                }
        stage('Push Image to Registry') {
            when {
                expression {
                    return env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'master'
                }
            }
            steps {
                script {
                    // Tag and push the Docker image
                    echo 'Push Application Image to DockerHub...'
                    bat "docker-compose -f ${DOCKER_COMPOSE_FILE} push"
                }
            }
        }
    }

    post {
            // Job to be performed after executing the build job.
       always {
             //emailext from: "stuti.shri2308@gmail.com",
             //to: "stuti.shri2308@gmail.com",
             //subject: "Jenkins Build Information ${JOB_NAME}#${BUILD_NUMBER}",
             //body: "Information: The build ${BUILD_NUMBER} of ${JOB_NAME} is ${currentBuild.currentResult}"

             echo 'The build completed...'
       }

    }
}
