pipeline {
    agent any
    environment {
        registry = 'alehad/msgr'
        jenkins_credentials = 'hub.docker.id'
        app_docker_image = ''
    }
    stages {
        stage('start services') {
            agent {
                docker {
                    image 'docker/compose' 
                }
            }
            steps {
                dir('devops/docker') {
                    sh 'docker compose -v'
                    sh 'docker compose -f msgr-pipeline-services-compose.yaml up'
                    sh 'docker compose ps'
                    sh 'mongo'
                }
            }
        }
    }
    post {
    	always {
    		sh 'docker-compose -f ./devops/docker/msgr-pipeline-services-compose.yaml down'
    	}
    }
}