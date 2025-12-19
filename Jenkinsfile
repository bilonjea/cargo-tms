pipeline {
	agent any

	stages {
		stage('Always Run') {
			steps {
				echo "Ce stage s'exécute sur toutes les branches : ${env.BRANCH_NAME}"
			}
		}

		stage('Build contracts-api (main)') {
			when {
				branch 'main'
			}
			steps {
				echo "Build contracts-api pour la branche main"
				dir('contracts-api') {
					sh 'mvn clean package'
				}
			}
		}

		stage('Build contracts-api (develop)') {
			when {
				branch 'develop'
			}
			steps {
				echo "Build contracts-api pour la branche develop"
				dir('contracts-api') {
					sh 'mvn clean verify'
				}
			}
		}

		stage('Build contracts-api (feature/*)') {
			when {
				expression { env.BRANCH_NAME.startsWith('feature/') }
			}
			steps {
				echo "Build contracts-api pour une branche feature/*"
				dir('contracts-api') {
					sh 'mvn clean install'
				}
			}
		}

	}
	post {
		always {
			archiveArtifacts artifacts: 'contracts-api/target/*.jar', fingerprint: true, allowEmptyArchive: true
			echo "Pipeline terminé pour la branche : ${env.BRANCH_NAME}"
		}
	}
}
