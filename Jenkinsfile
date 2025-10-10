pipeline {
  agent any
  options {
    skipDefaultCheckout(true)
    timestamps()
  }
  environment {
    JAVA_HOME            = tool(name: 'jdk-21', type: 'jdk')
    PATH                 = "${JAVA_HOME}/bin:${PATH}"

    # Maven
    MVN_FLAGS            = "-B -Dmaven.repo.local=${WORKSPACE}/.m2"
    # Images / registry
    REGISTRY             = "ghcr.io"
    IMAGE_NAME           = "ghcr.io/<org_ou_user>/tms"     // ← change-moi
    IMAGE_TAG            = "build-${BUILD_NUMBER}"
    # JFrog (generic repo ou maven repo)
    JFROG_URL            = "https://<your-artifactory-domain>"  // ← change-moi
    JFROG_REPO           = "libs-release-local"                  // ou libs-snapshot-local
    # Artefact principal backend
    TMS_JAR              = "tms/target/tms-*.jar"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        sh 'git submodule update --init --recursive || true'
      }
    }

    stage('Build contracts-api (install)') {
      steps {
        sh "mvn ${MVN_FLAGS} -pl contracts-api -am -DskipTests install"
      }
    }

    stage('Build tms (uses -U)') {
      steps {
        sh "mvn ${MVN_FLAGS} -U -pl tms -am -DskipTests install"
        stash name: 'tms-jar', includes: "${TMS_JAR}", useDefaultExcludes: false
      }
    }

    stage('Build tms-sb-admin') {
      steps {
        sh "mvn ${MVN_FLAGS} -pl tms-sb-admin -am -DskipTests package"
      }
    }

    stage('Build frontend (Angular + pnpm)') {
      steps {
        sh '''
          corepack enable || true
          corepack prepare pnpm@latest --activate
          cd frontend
          pnpm i
          pnpm build
        '''
        stash name: 'frontend-dist', includes: 'frontend/dist/**', useDefaultExcludes: false, allowEmpty: true
      }
    }

    stage('Tests & Analysis (tms)') {
      steps {
        sh "mvn ${MVN_FLAGS} -pl tms -am test"
      }
      post {
        always {
          junit testResults: 'tms/target/surefire-reports/*.xml', allowEmptyResults: true
          recordIssues enabledForFailure: true, tools: [java(), mavenConsole()]  // Warnings NG
          // Sonar (si configuré) :
          // withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          //   sh "mvn ${MVN_FLAGS} -pl tms -am sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.login=${SONAR_TOKEN}"
          // }
        }
      }
    }

    stage('Deploys (parallel)') {
      parallel {
        stage('Push artefact vers JFrog') {
          steps {
            unstash 'tms-jar'
            withCredentials([usernamePassword(credentialsId: 'jfrog-creds', usernameVariable: 'JF_USER', passwordVariable: 'JF_PASS')]) {
              sh '''
                FILE=$(ls -1 ''' + "${TMS_JAR}" + ''' | head -n1)
                DEST="${JFROG_URL}/artifactory/''' + "${JFROG_REPO}" + '''/tms/${BUILD_NUMBER}/$(basename "$FILE")"
                curl -u "${JF_USER}:${JF_PASS}" -T "$FILE" "$DEST"
              '''
            }
          }
        }

        stage('Deploy VM (SSH)') {
          steps {
            unstash 'tms-jar'
            withCredentials([sshUserPrivateKey(credentialsId: 'vm-ssh-key', keyFileVariable: 'KEY', usernameVariable: 'USER')]) {
              sh '''
                host=<vm_ip_or_dns>   # ← change-moi
                FILE=$(ls -1 ''' + "${TMS_JAR}" + ''' | head -n1)
                scp -i "$KEY" -o StrictHostKeyChecking=no "$FILE" "${USER}@${host}:/opt/tms/tms.jar"
                ssh -i "$KEY" -o StrictHostKeyChecking=no "${USER}@${host}" '
                  sudo systemctl stop tms || true
                  sudo systemctl start tms || sudo nohup java -jar /opt/tms/tms.jar --spring.profiles.active=prod >/var/log/tms.log 2>&1 &
                '
              '''
            }
          }
        }

        stage('Dockerize & Push (GHCR)') {
          steps {
            sh '''
              docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest -f tms/Dockerfile .
              echo "${GITHUB_TOKEN}" | docker login ${REGISTRY} -u ${GITHUB_USER} --password-stdin
              docker push ${IMAGE_NAME}:${IMAGE_TAG}
              docker push ${IMAGE_NAME}:latest
            '''
          }
          environment {
            GITHUB_USER  = credentials('ghcr-user')    // user ou PAT user
            GITHUB_TOKEN = credentials('ghcr-token')   // PAT avec scope package:write
          }
        }
      }
    }
  }

  post {
    success { echo "✔ Pipeline OK — build #${BUILD_NUMBER}" }
    always  { cleanWs(deleteDirs: true, notFailBuild: true) }
  }
}
