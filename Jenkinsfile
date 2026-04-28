pipeline {
    agent any

    environment {
        APP_NAME = "sme-backend"
        IMAGE_NAME = "sme-fastapi-prod"
        DB_URL = credentials('sme-db-url')
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST = "http://sonarqube:9000"
        MAIL_HOST = "mailpit"
    }

    stages {
        stage('Build Image') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} ./backend"
                sh "docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest"
            }
        }

        stage('SAST Analysis (SonarQube)') {
            steps {
                // Using the Sonar Scanner CLI image to scan the code
                sh """
                    docker run --rm \
                    --network sme_sme-network \
                    -v ${WORKSPACE}/backend:/usr/src \
                    sonarsource/sonar-scanner-cli \
                    -Dsonar.projectKey=sme-loan-backend \
                    -Dsonar.sources=. \
                    -Dsonar.host.url=${SONAR_HOST} \
                    -Dsonar.login=${SONAR_TOKEN}
                """
            }
        }

        stage('Unit Tests') {
            agent {
                docker { image 'python:3.10-slim' }
            }
            steps {
                sh 'pip install -r backend/requirements.txt'
                sh 'pytest backend/tests'
            }
        }

        stage('Deploy Backend') {
            steps {
                script {
                    sh "docker ps -aq --filter name=${APP_NAME} | xargs -r docker rm -f"

                    sh """
                        docker run -d \
                        --name ${APP_NAME} \
                        --network sme_sme-network \
                        -p 8000:8000 \
                        -e DATABASE_URL=${DB_URL} \
                        -e REDIS_URL=redis://sme-redis:6379/0 \
                        -e MAIL_SERVER=${MAIL_HOST} \
                        -e MAIL_PORT=1025 \
                        ${IMAGE_NAME}:latest
                    """
                }
            }
        }
    }
}