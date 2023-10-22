import os
import shutil

# path = './Geolife Trajectories 1.3/data by date/'
# for file_name in os.listdir(path):
#     if os.path.isfile(path + file_name):
#         dir = path + file_name.__str__()[:8] + '/'
#         if not os.path.exists(dir):
#             os.mkdir(dir)
#         shutil.move(path + file_name, dir + file_name)
#     else:
#         num = len(os.listdir(path + file_name + '/'))
#         dir = path + num.__str__() + '/'
#         if not os.path.exists(dir):
#             os.mkdir(dir)
#         shutil.move(path + file_name, dir + file_name)

path = './Geolife Trajectories 1.3/data by person/'
dst_path = './Geolife Trajectories 1.3/data/'
for file_name in os.listdir(path):
    if os.path.isfile(path + file_name):
        pass
    else:
        dir = path + file_name + '/Trajectory/'
        for pltfile in os.listdir(dir):
            temp = pltfile
            while os.path.exists(dst_path + temp):
                temp = 'a' + temp
            shutil.move(dir + pltfile, dst_path + temp)