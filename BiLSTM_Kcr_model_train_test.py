from keras import regularizers
from keras.callbacks import ModelCheckpoint, EarlyStopping
from keras.layers import Input, Dense, Flatten, MaxPooling2D, Dropout, Reshape, normalization,Bidirectional,LSTM,Embedding
from keras.layers.recurrent import LSTM
from keras.models import Model, Sequential
from keras.optimizers import SGD
from keras.regularizers import l1, l2
from keras.utils import np_utils
from keras.utils import plot_model
from keras.utils import to_categorical
from matplotlib.pyplot import MultipleLocator
from matplotlib.ticker import FormatStrFormatter
from sklearn import metrics
from sklearn.metrics import auc
from sklearn.metrics import roc_curve
from sklearn.model_selection import KFold
import keras.backend as K
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import random
import tensorflow as tf

#BiLSTM-Kcr
def baseline_model_getter():
    max_features = 21
    Embedding_out_size = 256
    lstm_units = 128
    model = Sequential()
    model.add(Embedding(max_features, Embedding_out_size, name="embedd_1"))
    model.add(Bidirectional(LSTM(lstm_units, kernel_regularizer=l2(0.001),bias_regularizer=regularizers.l2(0.0001),
                            dropout=0.2,return_sequences=True, name="bilstm_1")))
    model.add(LSTM(64, return_sequences=False, name="lstm_1", kernel_regularizer=l2(0.001),bias_regularizer=regularizers.l2(0.0001)))
    model.add(Dense(32, activation = 'relu', name="dense_1",kernel_regularizer = l2(1e-5),bias_regularizer=regularizers.l2(0.0001)))
    model.add(Dense(1, activation='sigmoid', name="dense_2",kernel_regularizer = l2(1e-5),bias_regularizer=regularizers.l2(0.0001)))  # softsign
    model.compile(optimizer='RMSProp',
                  loss='binary_crossentropy',  # 'mean_squared_error',
                  metrics=['acc'])

    return model

def dnn_model(lstm_model, train_X, train_Y, test_X, test_Y, lr, epoch, model_name, mean_tpr, mean_fpr,
              batch_size, indep_X, indep_Y):
    train_X = np.expand_dims(train_X, 2)
    test_X = np.expand_dims(test_X, 2)
    lstm_model.compile(optimizer='RMSProp',
                  loss='mean_squared_error',
                  metrics=['acc'])
    print("compile")
    lstm_model.fit(train_X, train_Y,  verbose=2,epochs=epoch, batch_size=batch_size, validation_data=(test_X, test_Y),
              shuffle=True,use_multiprocessing=True)

    lstm_model.save(data_path+'BiLSTM_mode-'+model_name+'.h5') #save model
    pre_test_y = lstm_model.predict(test_X, batch_size = 50)
    pre_train_y = lstm_model.predict(train_X, batch_size = 50)
    pre_indep_y = lstm_model.predict(indep_X)
    test_auc = metrics.roc_auc_score(test_Y, pre_test_y)
    train_auc = metrics.roc_auc_score(train_Y, pre_train_y)
    indep_auc = metrics.roc_auc_score(indep_Y, pre_indep_y)

    test_precision =metrics.average_precision_score(test_Y, pre_test_y)
    indep_precision = metrics.average_precision_score(indep_Y, pre_indep_y)
    print("train_auc: ", train_auc)
    print("test_auc: ", test_auc)

    y_pret = lstm_model.predict(test_X).ravel()

    fpr, tpr, thresholds = roc_curve(test_Y, y_pret)

    mean_tpr += np.interp(mean_fpr, fpr, tpr)

    roc_auc = auc(fpr, tpr)

    return test_auc, indep_auc, mean_tpr, mean_fpr, test_precision, indep_precision, pre_indep_y

data_path="/media/yang/data/numStr/"

model_name='human_BiLSTM'
posi_sample_size=9964
data = np.array(pd.read_csv(data_path+"human_posiNegaNum.list"))#inputfile
X1 = data[0:posi_sample_size, 1:]#6975 is the number of positive samples in training set, '1' is the label of positive sample
Y1 = data[0:posi_sample_size, 0]#'0' is the label of negative sample
X2 = data[posi_sample_size:, 1:]
Y2 = data[posi_sample_size:, 0]
X = np.concatenate([X1, X2], 0)
Y = np.concatenate([Y1, Y2], 0)

indep_sample_size=2540
indep_data = np.array(pd.read_csv(data_path+"human_indep_posiNegaNum.list"))#inputfile
indep_X1 = indep_data[0:indep_sample_size, 1:]
indep_Y1 = indep_data[0:indep_sample_size, 0]
indep_X2 = indep_data[indep_sample_size:, 1:]
indep_Y2 = indep_data[indep_sample_size:, 0]
indep_X = np.concatenate([indep_X1, indep_X2], 0)
indep_Y = np.concatenate([indep_Y1, indep_Y2], 0)


lr = 0.2 #learning rate
epoch = 25
batch_size = 50
kf = KFold(n_splits = 10, shuffle = True, random_state = 42)
kf = kf.split(X)

mean_tpr = 0.0
mean_fpr = np.linspace(0, 1, 100)
cnt = 0

test_aucs = []
acc_test=[]
for i, (train_fold, validate_fold) in enumerate(kf):
    print("\n\ni: ", i)
    init_model=baseline_model_getter()

    test_auc, indep_auc, mean_tpr, mean_fpr, test_precision, indep_precision, pre_indep_y = \
        dnn_model(init_model, X[train_fold],Y[train_fold],X[validate_fold],Y[validate_fold],lr, epoch,model_name,
                  mean_tpr,mean_fpr,data_path + "ROC_"+model_name+"_" + str(i) + ".txt", batch_size,indep_X, indep_Y)
    test_aucs.append(test_auc)

    acc_test.append(test_precision)
    cnt+=1

mean_tpr /= cnt 
mean_tpr[-1] = 1.0  
mean_auc = auc(mean_fpr, mean_tpr)

f= open(data_path+"ROC_mean_"+model_name+"_result.txt","a")
f.write(str(test_aucs))
f.write(str(acc_test))

f.write(str(mean_fpr))
f.write(str(mean_tpr))
f.close()

w = open(data_path+"BiLSTM-Kcr_"+model_name+"-result.txt", "w")
for j in test_aucs:
    w.write(str(j) + ',')
w.write('\n')
w.write(str(np.mean(test_aucs)) + '\n')
w.close()