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
        stage('Build') {
            steps {
                // Run any tests
                bat "docker-compose -f ${DOCKER_COMPOSE_FILE} up -d"
                echo 'Build completed...'
            }
        }
        stage('Test') {
                    steps {
                        script {
                            // Build the image using Docker Compose
                            //bat "docker-compose -f ${DOCKER_COMPOSE_FILE} build"
                            echo 'Testing...'
                        }
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
                    bat "docker-compose -f ${DOCKER_COMPOSE_FILE} push"
                    echo 'Push Image...'
                }
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                // You can deploy the container or service if needed
            }
        }
    }
    post {
        always {
            // Bring down the Docker Compose services and clean up
            bat "docker-compose -f ${DOCKER_COMPOSE_FILE} down"
            echo 'Completed...'
        }
    }
}
