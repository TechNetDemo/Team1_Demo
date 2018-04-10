pipeline {
    agent none
    stages{
        stage('start_approval'){
            steps{
            input message: 'ready to start?', submitter: 'admin,sit_admin'
            }
        }
        
        stage('Prerequisite'){
            steps{
            echo '<Deploy DB, tomcat, etc.>'
            }
        }
        stage('WebApp'){
            steps{
            build job: '/TopLevelPipelineDemo/SIT/Team1_SIT_DownloadWebApp', parameters: [string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/SIT/Team1_SIT_ReplaceWebAppToken', parameters: [string(name: 'db_username', value: db_username), string(name: 'db_password', value: db_password), string(name: 'db_url', value: db_url), string(name: 'db_tableName', value: db_tableName), string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/SIT/Team1_SIT_DeployWebApp', parameters: [[$class: 'NodeParameterValue', name: 'node_to_run', allowMultiNodeSelection: true,triggerConcurrentBuilds: true,labels: ['SITWIN001','SITWIN002'], nodeEligibility: [$class: 'AllNodeEligibility'], triggerIfResult: 'allCases']]

            }
        }

    }
    post {
        success{
        input message:'SIT deployment success. Start PROD deployment?',submitter:'admin,sit_admin'
        }
        failure {
            echo '<rollback process>'
        }
    }
    
}