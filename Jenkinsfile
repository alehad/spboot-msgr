pipeline {
    agent any
    environment {
        registry = 'alehad/msgr'
        jenkins_credentials = 'hub.docker.id'
        app_docker_image = ''
    }
    stages {
        stage('start services') {
            agent { label 'master' }
            steps {
            	sh '''
            		docker --version
            		docker compose version
            	'''
            }
        }
    }
}