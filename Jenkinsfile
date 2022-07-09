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
                sh 'docker-compose -f ./devops/docker/msgr-pipeline-services-compose.yaml up'
                sh 'docker ps'
                sh 'mongo'
            }
        }
    }
    post {
    	always {
    		sh 'docker-compose -f ./devops/docker/msgr-pipeline-services-compose.yaml down'
    	}
    }
}