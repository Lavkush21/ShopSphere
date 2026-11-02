
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'ShopSphere source code checked out'
            }
        }

        stage('Build') {
            steps {
                bat 'mvnw.cmd -B clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvnw.cmd -B test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvnw.cmd -B package -DskipTests'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'ShopSphere CI Pipeline SUCCESS!'
        }
        failure {
            echo 'ShopSphere CI Pipeline FAILED!'
        }
    }
}
