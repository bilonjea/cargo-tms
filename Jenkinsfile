pipeline {
  agent { label 'docker' }

  environment {
    JFROG_CREDENTIALS = credentials('jfrog-credentials')
    SONAR_TOKEN = credentials('sonar-token')
    NPM_TOKEN = credentials('npm-token')
    VM_USER = 'user'
    VM_HOST = 'vm.example.com'
    VM_BACKEND_PATH = '/opt/cargo-tms/'
    VM_FRONTEND_PATH = '/var/www/cargo-tms/'
    SLACK_CHANNEL = '#cicd'
    JIRA_SITE = 'JIRA_SITE'
    JIRA_ISSUE = 'PROJ-123'
    MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
  }

  stages {
    stage('Checkout & Build Maven') {
      steps {
        dir('contracts-api') {
          sh 'mvn clean install'
        }
        dir('tms') {
          sh 'mvn clean package'
        }
      }
      post {
        failure {
          echo "Build Maven échoué – Voir les logs."
          script { currentBuild.result = 'FAILURE' }
        }
      }
    }
    stage('Analyse SpotBugs & Warnings') {
      steps {
        dir('contracts-api') {
          sh 'mvn spotbugs:spotbugs'
          recordIssues tool: spotBugs()
        }
        dir('tms') {
          sh 'mvn spotbugs:spotbugs'
          recordIssues tool: spotBugs()
          sh 'mvn compile compiler:compile warnings:warn'
          recordIssues tool: mavenConsole()
        }
      }
      post {
        failure {
          echo "Des bugs critiques ont été détectés – Corriger avant de continuer."
          script { currentBuild.result = 'FAILURE' }
        }
      }
    }
    stage('Tests & Analyse SonarQube') {
      steps {
        dir('contracts-api') {
          sh 'mvn test jacoco:report'
          jacoco(execPattern: '**/target/jacoco.exec')
          withSonarQubeEnv('SonarQube-Server') {
            sh "mvn sonar:sonar -Dsonar.projectKey=cargo-tms-contracts-api -Dsonar.login=${SONAR_TOKEN}"
          }
        }
        dir('tms') {
          sh 'mvn test jacoco:report'
          jacoco(execPattern: '**/target/jacoco.exec')
          withSonarQubeEnv('SonarQube-Server') {
            sh "mvn sonar:sonar -Dsonar.projectKey=cargo-tms-tms -Dsonar.login=${SONAR_TOKEN}"
          }
        }
        //dir('frontend') {
          //sh 'pnpm install'
          //sh 'pnpm test --watch=false --code-coverage'
        //}
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
          jacoco()
        }
        failure {
          echo "Coverage insuffisant ou analyse SonarQube échouée."
          script { currentBuild.result = 'FAILURE' }
        }
      }
    }
    stage('Vérification Coverage') {
      steps {
        script {
          def coverage = jacoco().instructionCoverage
          if (coverage < 80) {
            error "Coverage JaCoCo insuffisant: ${coverage}% (< 80%)"
          }
        }
      }
    }
    stage('Publication Artefacts') {
      steps {
        dir('contracts-api') {
          sh "mvn deploy -DskipTests -DaltDeploymentRepository=repo::default::https://jfrog.example.com/artifactory/libs-release-local"
        }
        dir('tms') {
          sh "mvn deploy -DskipTests -DaltDeploymentRepository=repo::default::https://jfrog.example.com/artifactory/libs-release-local"
        }
        //dir('frontend') {
        //  sh 'pnpm build'
        //  sh 'pnpm publish --registry=https://npm.pkg.github.com'
        //}
      }
    }
    stage('Déploiement VM') {
      steps {
        sh "scp tms/tms-service/target/*.jar ${VM_USER}@${VM_HOST}:${VM_BACKEND_PATH}"
        sh "ssh ${VM_USER}@${VM_HOST} 'sudo systemctl restart cargo-tms'"
        sh "scp -r frontend/dist/* ${VM_USER}@${VM_HOST}:${VM_FRONTEND_PATH}"
      }
    }
    stage('Tests de Performance & Sécurité') {
      steps {
        sh 'jmeter -n -t tests/performance.jmx -l results.jtl -e -o reports/'
        archiveArtifacts artifacts: 'reports/**', fingerprint: true
        dir('tms') {
          sh 'mvn org.owasp:dependency-check-maven:check'
          dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
        }
        // sh 'trivy image my-docker-image:latest'
      }
      post {
        failure {
          echo "Tests de performance ou scans de sécurité échoués."
          script { currentBuild.result = 'FAILURE' }
        }
      }
    }
  }
  post {
    always {
      slackSend channel: "${SLACK_CHANNEL}", message: "Pipeline ${currentBuild.currentResult}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
      jiraSend site: "${JIRA_SITE}", issueKey: "${JIRA_ISSUE}", comment: "Build ${currentBuild.currentResult}"
    }
    failure {
      echo "Pipeline échoué – Notifications envoyées."
    }
    success {
      echo "Pipeline réussi – Notifications envoyées."
    }
  }
}
