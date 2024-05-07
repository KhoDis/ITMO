import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.datasets import fetch_openml
from sklearn.model_selection import cross_val_score

df = pd.read_csv('lab1/credit-g.csv')

# @attribute 'checking_status' { '<0', '0<=X<200', '>=200', 'no checking'}
# @attribute 'duration' real
# @attribute 'credit_history' { 'no credits/all paid', 'all paid', 'existing paid', 'delayed previously', 'critical/other existing credit'}
# @attribute 'purpose' { 'new car', 'used car', furniture/equipment, radio/tv, 'domestic appliance', repairs, education, vacation, retraining, business, other}
# @attribute 'credit_amount' real
# @attribute 'savings_status' { '<100', '100<=X<500', '500<=X<1000', '>=1000', 'no known savings'}
# @attribute 'employment' { unemployed, '<1', '1<=X<4', '4<=X<7', '>=7'}
# @attribute 'installment_commitment' real
# @attribute 'personal_status' { 'male div/sep', 'female div/dep/mar', 'male single', 'male mar/wid', 'female single'}
# @attribute 'other_parties' { none, 'co applicant', guarantor}
# @attribute 'residence_since' real
# @attribute 'property_magnitude' { 'real estate', 'life insurance', car, 'no known property'}
# @attribute 'age' real
# @attribute 'other_payment_plans' { bank, stores, none}
# @attribute 'housing' { rent, own, 'for free'}
# @attribute 'existing_credits' real
# @attribute 'job' { 'unemp/unskilled non res', 'unskilled resident', skilled, 'high qualif/self emp/mgmt'}
# @attribute 'num_dependents' real
# @attribute 'own_telephone' { none, yes}
# @attribute 'foreign_worker' { yes, no}
# @attribute 'class' { good, bad}

df.columns = df.columns.str.replace("'", "")

df.hist(bins=50, figsize=(20,15))
plt.show()

num_attributes = ['duration', 'credit_amount', 'installment_commitment', 'residence_since', 'age', 'existing_credits', 'num_dependents']

cat_attributes = list(set(df.columns) - set(num_attributes))

for cat in cat_attributes:
    df[cat] = df[cat].astype('category').cat.codes

X = df.drop('class', axis=1)
y = df['class']

real_num_attributes = ['duration', 'credit_amount', 'age']

for num in real_num_attributes:
    plt.figure()
    df.boxplot(column=num)
    plt.title(num)
    plt.show()

X[real_num_attributes].hist(figsize=(20,15))
plt.show()

from scipy import stats
z_scores = stats.zscore(df[real_num_attributes])

df = df[(abs(z_scores) < 3).all(axis=1)]

for num in real_num_attributes:
    plt.figure()
    df.boxplot(column=num)
    plt.title(f'{num} without outliers')
    plt.show()

scaler = StandardScaler()
X[real_num_attributes] = scaler.fit_transform(X[real_num_attributes])

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
X_train, X_val, y_train, y_val = train_test_split(X_train, y_train, test_size=0.2, random_state=42)

import optuna
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.naive_bayes import GaussianNB
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier

knn_scores = []
svc_scores = []
gnb_scores = []
dtc_scores = []
rfc_scores = []
index = 0

def objective(trial):
    regressor = trial.suggest_categorical('regressor', [
        'KNN',
        'SVC',
        'GaussianNB',
        'DecisionTree',
        'RandomForest',
    ])
    if regressor == 'KNN':
        n_neighbors = trial.suggest_int('n_neighbors', 1, 50)
        weights = trial.suggest_categorical('weights', ['uniform', 'distance'])
        algorithm = trial.suggest_categorical('algorithm', ['ball_tree', 'kd_tree'])
        leaf_size = trial.suggest_int('leaf_size', 1, 50)
        p = trial.suggest_int('p', 1, 2)
        metric = trial.suggest_categorical('metric', ['minkowski', 'euclidean', 'manhattan'])

        print(f'KNN: n_neighbors={n_neighbors}, weights={weights}, algorithm={algorithm}, leaf_size={leaf_size}, p={p}, metric={metric}')
        clf = KNeighborsClassifier(n_neighbors=n_neighbors, weights=weights, algorithm=algorithm, leaf_size=leaf_size, p=p, metric=metric, n_jobs=-1)
    elif regressor == 'SVC':
        C = trial.suggest_loguniform('C', 1e-3, 1e3)
        kernel = trial.suggest_categorical('kernel', ['poly', 'rbf', 'sigmoid'])
        print(f'SVC: C={C}, kernel={kernel}')
        clf = SVC(C=C, kernel=kernel)
    elif regressor == 'GaussianNB':
        var_smoothing = trial.suggest_loguniform('var_smoothing', 1e-10, 1)

        print(f'GaussianNB: var_smoothing={var_smoothing}')
        clf = GaussianNB(var_smoothing=var_smoothing)
    elif regressor == 'DecisionTree':
        criterion = trial.suggest_categorical('criterion', ['gini', 'entropy'])
        max_depth = trial.suggest_int('max_depth', 1, 50)
        min_samples_split = trial.suggest_int('min_samples_split', 2, 10)
        min_samples_leaf = trial.suggest_int('min_samples_leaf', 1, 10)

        print(f'DecisionTree: criterion={criterion}, max_depth={max_depth}, min_samples_split={min_samples_split}, min_samples_leaf={min_samples_leaf}')
        clf = DecisionTreeClassifier(criterion=criterion, max_depth=max_depth, min_samples_split=min_samples_split, min_samples_leaf=min_samples_leaf)
    elif regressor == 'RandomForest':
        n_estimators = trial.suggest_int('n_estimators', 1, 100)
        criterion = trial.suggest_categorical('criterion', ['gini', 'entropy'])
        max_depth = trial.suggest_int('max_depth', 1, 50)
        min_samples_split = trial.suggest_int('min_samples_split', 2, 10)
        min_samples_leaf = trial.suggest_int('min_samples_leaf', 1, 10)

        print(f'RandomForest: n_estimators={n_estimators}, criterion={criterion}, max_depth={max_depth}, min_samples_split={min_samples_split}, min_samples_leaf={min_samples_leaf}')
        clf = RandomForestClassifier(n_estimators=n_estimators, criterion=criterion, max_depth=max_depth, min_samples_split=min_samples_split, min_samples_leaf=min_samples_leaf, n_jobs=-1)

    clf.fit(X_train, y_train)
    score = clf.score(X_val, y_val)

    global index

    if regressor == 'KNN':
        knn_scores.append((index, score))
    elif regressor == 'SVC':
        svc_scores.append((index, score))
    elif regressor == 'GaussianNB':
        gnb_scores.append((index, score))
    elif regressor == 'DecisionTree':
        dtc_scores.append((index, score))
    elif regressor == 'RandomForest':
        rfc_scores.append((index, score))
    index += 1
    return score

study = optuna.create_study(direction='maximize')
study.optimize(objective, n_trials=100)

import matplotlib.pyplot as plt
import numpy as np

knn_scores = np.array(knn_scores)
svc_scores = np.array(svc_scores)
gnb_scores = np.array(gnb_scores)
dtc_scores = np.array(dtc_scores)
rfc_scores = np.array(rfc_scores)

plt.plot(knn_scores[:, 0], knn_scores[:, 1], label='KNN')
plt.plot(svc_scores[:, 0], svc_scores[:, 1], label='SVC')
plt.plot(gnb_scores[:, 0], gnb_scores[:, 1], label='GaussianNB')
plt.plot(dtc_scores[:, 0], dtc_scores[:, 1], label='DecisionTree')
plt.plot(rfc_scores[:, 0], rfc_scores[:, 1], label='RandomForest')
plt.title('Scores for each regressor')
plt.legend()
plt.show()

print('Best hyperparameters: ', study.best_params)

best_algorithm = study.best_params['regressor']

if best_algorithm == 'KNN':
    model = KNeighborsClassifier(n_neighbors=study.best_params['n_neighbors'], weights=study.best_params['weights'], algorithm=study.best_params['algorithm'], leaf_size=study.best_params['leaf_size'], p=study.best_params['p'], metric=study.best_params['metric'], n_jobs=-1)
elif best_algorithm == 'SVC':
    model = SVC(C=study.best_params['C'], kernel=study.best_params['kernel'])
elif best_algorithm == 'GaussianNB':
    model = GaussianNB(var_smoothing=study.best_params['var_smoothing'])
elif best_algorithm == 'DecisionTree':
    model = DecisionTreeClassifier(criterion=study.best_params['criterion'], max_depth=study.best_params['max_depth'], min_samples_split=study.best_params['min_samples_split'], min_samples_leaf=study.best_params['min_samples_leaf'])
elif best_algorithm == 'RandomForest':
    model = RandomForestClassifier(n_estimators=study.best_params['n_estimators'], criterion=study.best_params['criterion'], max_depth=study.best_params['max_depth'], min_samples_split=study.best_params['min_samples_split'], min_samples_leaf=study.best_params['min_samples_leaf'], n_jobs=-1)

model.fit(X_train, y_train)

print('Results on the validation set:')
y_pred = model.predict(X_val)
print(classification_report(y_val, y_pred))
print('Confusion matrix:')
print(confusion_matrix(y_val, y_pred))

print('Results on the test set:')
y_pred = model.predict(X_test)
print(classification_report(y_test, y_pred))
print('Confusion matrix:')
print(confusion_matrix(y_test, y_pred))

from sklearn.metrics import plot_confusion_matrix
plot_confusion_matrix(model, X_test, y_test, cmap=plt.cm.Blues)
plt.show()

from sklearn.metrics import plot_roc_curve, plot_precision_recall_curve
plot_roc_curve(model, X_test, y_test)
plt.show()

from sklearn.model_selection import RandomizedSearchCV
from scipy.stats import randint, uniform

param_distributions = {
    'KNN': {
        'n_neighbors': randint(1, 100),
        'weights': ['uniform', 'distance'],
        'algorithm': ['auto', 'ball_tree', 'kd_tree', 'brute'],
        'leaf_size': randint(1, 100),
        'p': [1, 2],
        'metric': ['minkowski', 'euclidean', 'manhattan']
    },
    'SVC': {
        'C': uniform(0.1, 10),
        'kernel': ['poly', 'rbf', 'sigmoid']
    },
    'GaussianNB': {
        'var_smoothing': uniform(1e-9, 1e-6)
    },
    'DecisionTree': {
        'criterion': ['gini', 'entropy'],
        'max_depth': randint(1, 50),
        'min_samples_split': randint(2, 10),
        'min_samples_leaf': randint(1, 10)
    },
    'RandomForest': {
        'n_estimators': randint(1, 100),
        'criterion': ['gini', 'entropy'],
        'max_depth': randint(1, 50),
        'min_samples_split': randint(2, 10),
        'min_samples_leaf': randint(1, 10)
    }
}

classifiers = {
    'KNN': KNeighborsClassifier(),
    'SVC': SVC(),
    'GaussianNB': GaussianNB(),
    'DecisionTree': DecisionTreeClassifier(),
    'RandomForest': RandomForestClassifier()
}

random_searchers = {}
for algorithm in param_distributions.keys():
    random_searchers[algorithm] = RandomizedSearchCV(classifiers[algorithm], param_distributions[algorithm], n_iter=100, cv=5, n_jobs=-1, random_state=42)

for algorithm in random_searchers.keys():
    print(f'Fitting {algorithm}...')
    random_searchers[algorithm].fit(X_train, y_train)

for algorithm in random_searchers.keys():
    plt.plot(np.arange(0, len(random_searchers[algorithm].cv_results_['mean_test_score'])), random_searchers[algorithm].cv_results_['mean_test_score'], label=algorithm)
plt.title('Scores for each regressor (random search)')
plt.legend()
plt.show()


for algorithm in random_searchers.keys():
    print(f'Evaluation of {algorithm}:')
    print(random_searchers[algorithm].best_score_)
    print(random_searchers[algorithm].best_params_)
print()

for algorithm in random_searchers.keys():
    print(f'Evaluation of {algorithm}:')
    print(random_searchers[algorithm].score(X_test, y_test))
    print(random_searchers[algorithm].best_params_)
print()

for algorithm in random_searchers.keys():
    plot_roc_curve(random_searchers[algorithm], X_test, y_test)
    plt.title(algorithm)
plt.show()

# Сравнение

print('Optuna:')
print(study.best_params)
print(study.best_value)
print()

print('Random search:')
for algorithm in random_searchers.keys():
    print(f'{algorithm}:')
    print(random_searchers[algorithm].best_params_)
    print(random_searchers[algorithm].best_score_)
    print()

# Изменение ошибки в зависимости от итерации

import matplotlib.pyplot as plt
plt.plot(study.trials_dataframe().values[:, 1])
plt.xlabel('Iteration')
plt.ylabel('Error')
plt.show()

import optuna.visualization as optvis
fig = optvis.plot_param_importances(study, params=list(study.best_params.keys()))
fig.show()