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
       stage('Builds en parallèle') {
      parallel {
        stage('Frontend (React)') {
            steps {
        sh '''
         cd frontend
         npm install || true
        '''
        }
        }
        stage('build tms') {
            steps {
        sh '''
          cd tms
          mvn -U clean verify || true
        '''
        }
        }
      }
    }
        
    }
}
