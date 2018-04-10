pipeline {
    agent none
    stages{
        stage('start_approval'){
            steps{
            input message: 'ready to start?', submitter: 'admin,dev_admin', parameters: [string(defaultValue: '0.1.0-SNAPSHOT', name: 'artifact_version', trim: false)]
            }
        }
        
        stage('Prerequisite'){
            steps{
            echo '<Deploy DB, tomcat, etc.>'
            }
        }
        stage('WebApp'){
            steps{
            build job: '/TopLevelPipelineDemo/DEV/Team1_DEV_DownloadWebApp', parameters: [string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/DEV/Team1_DEV_ReplaceWebAppToken', parameters: [string(name: 'db_username', value: db_username), string(name: 'db_password', value: db_password), string(name: 'db_url', value: db_url), string(name: 'db_tableName', value: db_tableName), string(name: 'artifact_version', value: artifact_version)]
            build job: '/TopLevelPipelineDemo/DEV/Team1_DEV_DeployWebApp', parameters: [[$class: 'NodeParameterValue', name: 'node_to_run', allNodesMatchingLabel: true,labels: [node_to_run], nodeEligibility: [$class: 'AllNodeEligibility'], triggerIfResult: 'allCases']]

            }
        }

    }
    post {
        success{
        input message:'DEV deployment success. Start SIT deployment?',submitter:'admin,dev_admin'
        }
        failure {
            echo '<rollback process>'
        }
    }
    
}