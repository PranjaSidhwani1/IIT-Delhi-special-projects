import pandas as pd
import sys


# Instructions for participants :
'''
Participants can use this code to run on labeled train/out-of-sample data to mimic evaluation process.
### Datasets required:
This script takes in 3 files as follows:

primary_submission.csv -> This contains the match_id, dataset_type, win_pred_team_id, win_pred_score, train_algorithm, is_ensemble, train_hps_trees, train_hps_depth, train_hps_lr, *top 10 feature values. This is file submitted by participant.
secondary_submission.csv -> This contains feature_name, feature_description, model_feature_importance_rank, model_feature_importance_percentage, feature_correlation_dep_var. This is file submitted by participant.
dep_var.csv    -> This contains match_id, dataset_type, win_team_id. Participants can generate from the labeled train data.

Please ensure that the predicted_score column does not have any null columns and the column names are exactly matching as above.
Please ensure that all these files are stored as ',' separated csv files.

### How to use:
To use this, first open the command line terminal, and call evaluation code script by passing the locations of submission and actual files respectively.
Sample example of using commandline for running the script: 

python Evaluation_Code.py path_to_primary_submission_file path_to_secondary_submission_file path_to_DepVar_file
'''


def checkDataType1(df):
    assert (df['match id'].isna().sum() == 0), 'match id should not have NaNs'
    assert (df['match id'].dtype == 'int64'), ('match id is not int64 type')
    assert df['win_pred_team_id'].isna().sum(
    ) == 0, 'win_pred_team_id should not have NaNs'
    assert df['win_pred_team_id'].dtype == 'int64', (
        'win_pred_team_id is not int64 type')
    assert df['win_pred_score'].isna().sum(
    ) == 0, 'win_pred_score should not have NaNs'
    assert df['win_pred_score'].dtype == 'float64', (
        'win_pred_score is not float64 type')
    assert df['train_algorithm'].isna().sum(
    ) == 0, 'train_algorithm should not have NaNs'
    assert df['train_algorithm'].dtype == 'object', (
        'train_algorithm is not object type')
    assert df['is_ensemble'].isna().sum(
    ) == 0, 'is_ensemble should not have NaNs'
    assert df['is_ensemble'].dtype == 'object', (
        'is_ensemble is not object type')
    assert df['train_hps_trees'].isna().sum(
    ) == 0, 'train_hps_trees should not have NaNs'
    assert df['train_hps_depth'].isna().sum(
    ) == 0, 'train_hps_depth should not have NaNs'
    assert df['train_hps_lr'].isna().sum(
    ) == 0, 'train_hps_lr should not have NaNs'
    return None


def checkDataType2(df):
    assert df['feat_id'].isna().sum() == 0, 'feat_id should not have NaNs'
    assert df['feat_id'].dtype == 'int64', ('feat_id is not int type')
    assert df['feat_name'].isna().sum() == 0, 'feat_name should not have NaNs'
    assert df['feat_name'].dtype == 'object', ('feat_name is not object type')
    assert df['feat_description'].isna().sum(
    ) == 0, 'feat_description should not have NaNs'
    assert df['feat_description'].dtype == 'object', (
        'feat_description is not object type')
    assert df['model_feat_imp_train'].isna().sum(
    ) == 0, ' model_feat_imp_train should not have NaNs'
    assert df['model_feat_imp_train'].dtype == 'float64', (
        'model_feat_imp_train is not float type')
    assert df['feat_rank_train'].isna().sum(
    ) == 0, 'feat_rank_train should not have NaNs'
    assert df['feat_rank_train'].dtype == 'int64', (
        'feat_rank_train is not int64 type')
    return None


def getAccuracy(df):
    return round(df[df['winner_id'] == df['win_pred_team_id']].shape[0]*100/df.shape[0], 4)

if len(sys.argv) != 4:
  sys.exit("Please pass three files only as mentioned in the Instructions.")

# Location of submission file. Header here should include match_id, dataset_type, win_team_id. The file should be comma separated.
input1_address = sys.argv[1]
df_input1 = pd.read_csv(input1_address, sep=",", header=0)

input2_address = sys.argv[2]
df_input2 = pd.read_csv(input2_address, sep=",", header=0)

# For participants Team : Location of Dependent Variable file. Header here would be match_id, dataset_type, win_team_id. Participants can generate from the labeled train data. These files are comma separated
round_eval = sys.argv[3]
df_round = pd.read_csv(round_eval, sep=",", header=0)

assert set(['match id', 'dataset_type', 'win_pred_team_id', 'win_pred_score', 'train_algorithm', 'is_ensemble', 'train_hps_trees',
           'train_hps_depth', 'train_hps_lr']).issubset(set(df_input1.columns.tolist())), 'Required columns not present in primary submission file'
assert set(['indep_feat_id1', 'indep_feat_id2', 'indep_feat_id3', 'indep_feat_id4', 'indep_feat_id5', 'indep_feat_id6', 'indep_feat_id7', 'indep_feat_id8',
           'indep_feat_id9', 'indep_feat_id10']).issubset(set(df_input1.columns.tolist())), 'Required indepedent feature columns not present in primary submission file'
assert set(['feat_id', 'feat_name', 'feat_description', 'model_feat_imp_train', 'feat_rank_train']).issubset(
    set(df_input2.columns.tolist())), 'Required columns not present in secondary submission file'

checkDataType1(df_input1)
checkDataType2(df_input2)

assert df_input1.shape[0] == df_input1.drop_duplicates(
    'match id').shape[0], 'Input file should be unique on match id'
# assert df_input1.shape[
#     0] == 1219, f'Input file size number of rows incorrect. Expected rowsize 1219 not equal to uploaded data rowsize {df_input1.shape[0]}'
assert df_input1.shape[1] == 19, 'Input file number of columns not correct. '
assert (df_input1.win_pred_score.min() >= 0) & (df_input1.win_pred_score.max(
) <= 1), 'Win prediction score should be in range [0,1]'
assert df_input1['train_algorithm'].nunique(
) == 1, 'only one algorithm can be used for all data'
assert (len(df_input1['is_ensemble'].unique().tolist()) == 1) & ((df_input1['is_ensemble'].unique().tolist()[
    0] == 'yes') | (df_input1['is_ensemble'].unique().tolist()[0] == 'no')), 'is_ensemble can take only \'yes\' or \'no\''
assert df_input1.apply(lambda x: 0 if (len(str(x['train_algorithm']).split(';')) == len(str(x['train_hps_trees']).split(';'))) &
                       (len(str(x['train_algorithm']).split(';')) == len(str(x['train_hps_depth']).split(';'))) & (len(str(x['train_algorithm']).split(';')) == len(str(x['train_hps_lr']).split(';'))) else 1, axis=1).max() == 0, 'number of fields in algorithm & hyper-parameters column should be same.'

'''
shape_before_join = df_round.shape[0]

r1_size = df_input1[df_input1['dataset_type'] == 'r1'].shape[0]
assert (r1_size ==
        df_round.shape[0]), f'R1 data size in input file is incorrect. Expected rowsize 271 not equal to r1 dataset_type present {r1_size}'
'''

# merging predicted file and dependent variable file
eval_data = pd.merge(df_round, df_input1, on=[
                     'match id'], how='inner').drop_duplicates()
assert (eval_data.shape[0] == df_round.shape[0]
        ), 'match ids in submission template does not match eval data'

print('All checks passed...')
print('Accuracy: ', round(getAccuracy(eval_data), 2))
