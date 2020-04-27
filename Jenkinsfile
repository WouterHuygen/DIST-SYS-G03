pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                sh 'echo "Hello World"'
				sh '''
					echo "Mutlilante shell steps works too"
					ls -lah
				'''
            }
        }
    }
}