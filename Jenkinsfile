pipeline {
    agent any
    environment {
        registry = 'alehad/msgr'
        jenkins_credentials = 'hub.docker.id'
        app_docker_image = ''
    }
    stages {
        stage('build msgr project') {
            agent {
                docker {
                    image 'maven:3.8.1-adoptopenjdk-11' 
                }
            }
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }
        stage('create docker image') {
            agent { label 'master' } // this ensures that jenkins will try to access docker running on same host jenkins is running
            steps {
                script {
                    app_docker_image = docker.build registry
                }
            }
        }
        stage('upload docker image') {
            agent { label 'master' }
            steps {
                script {
                    docker.withRegistry('', jenkins_credentials) {
                        app_docker_image.push("1.2")
                        app_docker_image.push("latest")
                    }
                }
            }
        }
    }
}