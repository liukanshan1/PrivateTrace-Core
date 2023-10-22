tnc = 206
tpc = 23
fnc = 0 # actual positive, predicted negative
fpc = 0 # acutal negative, predicted positive

print(tpc, fpc)
print(fnc, tnc)

print("accuracy:", (tpc + tnc) / (tpc + tnc + fpc + fnc))
precision = tpc / (tpc + fpc)
print("precision:", precision)
recall = tpc / (tpc + fnc)
print("recall:", recall)
print("F1-Score:", 2 * recall * precision / (recall + precision))