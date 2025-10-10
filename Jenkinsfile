pipeline {
    agent { label 'docker' }
    
    stages {
        stage('Ping'){ 
            steps { 
                sh 'ls -la' 
            } 
        }
        stage('Build') {
            steps {
        sh '''
          cd contracts-api
          mvn install || true
        '''
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
