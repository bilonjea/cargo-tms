pipeline {
    agent { label 'docker-1' }
    
    stages {
        stage('Ping'){ 
            steps { 
                sh 'hostname && whoami' 
            } 
        }
        stage('Build') {
            steps {
                echo 'Building cargo-tms...'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing cargo-tms...'
            }
        }
        stage('Dockerized'){
            agent { 
                docker { 
                    image 'alpine:3.19' 
                }
            }
            steps { 
                sh 'echo hello from container && cat /etc/alpine-release' 
            }
        }
    }
}
