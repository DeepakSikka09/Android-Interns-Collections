import cv2
import numpy as np
from tensorflow.keras.models import model_from_json

def real_spoof_detection(frame_path, cascade_path, json_path, weights_path):
    face_cascade = cv2.CascadeClassifier(cascade_path)
    json_file = open(json_path)
    loaded_model_json = json_file.read()
    json_file.close()
    antispoof_model = model_from_json(loaded_model_json)
    antispoof_model.load_weights(weights_path)
    frame = cv2.imread(frame_path)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)
    for (x, y, w, h) in faces:
        face = frame[y-5:y+h+5, x-5:x+w+5]
        resized_face = cv2.resize(face, (160, 160))
        resized_face = resized_face.astype("float") / 255.0
        resized_face = np.expand_dims(resized_face, axis=0)
        preds = antispoof_model.predict(resized_face)[0]
        return preds