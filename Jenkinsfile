pipeline {
	agent any

	stages {
		stage('always run') {
			steps {
				echo "ce stage s'exécute sur toutes les branches : ${env.branch_name}"
			}
		}


		stage('Build') {
            steps {
                echo 'Building project...'  // Pas de bloc `steps` explicite
                sh 'ls -l'
            }
        }

		stage('build contracts-api (main)') {
			when {
				branch 'main'
			}
			steps {
				echo "build contracts-api pour la branche main"
				dir('contracts-api') {
					sh 'mvn clean package'
				}
			}
		}

		stage('build contracts-api (release)') {
            when {
                branch 'release'
            }
            steps {
                sh '''
                    echo "build contracts-api pour la branche release"
                    cd contracts-api
                    mvn clean package
                    ls -l target
                '''
            }
        }

		stage('build contracts-api (develop)') {
			when {
				branch 'develop'
			}
			steps {
				echo "build contracts-api pour la branche develop"
				dir('contracts-api') {
					sh 'mvn clean verify'
				}
			}
		}

		stage('build contracts-api (feature/*)') {
			when {
				expression { env.branch_name.startsWith('feature/') }
			}
			steps {
				echo "build contracts-api pour une branche feature/*"
				dir('contracts-api') {
					sh 'mvn clean install'
				}
			}
		}

	}
	post {
		always {
			archiveArtifacts artifacts: 'contracts-api/target/*.jar', fingerprint: true, allowEmptyArchive: true
			echo "pipeline terminé pour la branche : ${env.branch_name}"
		}
	}
}
