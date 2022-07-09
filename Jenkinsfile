pipeline {
    agent any
    environment {
        registry = 'alehad/msgr'
        jenkins_credentials = 'hub.docker.id'
        app_docker_image = ''
    }
    stages {
        stage('start services') {
            steps {
                sh 'docker compose -d --no-color --wait -f ./devops/docker/msgr-pipeline-services-compose.yaml up'
                sh 'docker compose ps'
                sh 'mongo'
            }
        }
    }
    post {
    	always {
    		sh 'docker compose --remove-orphans -v -f ./devops/docker/msgr-pipeline-services-compose.yaml down'
    	}
    }
}