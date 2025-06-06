import cv2
import torch

# Charger le modèle (assure-toi d'utiliser le bon chemin pour 'best.pt')
model = torch.hub.load('ultralytics/yolov5', 'custom', path='best.pt', force_reload=True)

# Ouvrir la vidéo ou la caméra
cap = cv2.VideoCapture(0)  # Change '0' pour le chemin de ta vidéo si nécessaire

while True:
    ret, frame = cap.read()
    if not ret:
        break

    # Prétraiter l'image
    results = model(frame)

    # Boucle sur chaque détection
    for result in results.xyxy[0]:  # Accéder aux résultats de la première image
        x1, y1, x2, y2, conf, cls = result  # Décompose les résultats
        label = f"{model.names[int(cls)]} {conf:.2f}"  # Crée le label avec la classe et la confiance

        # Dessine la boîte englobante
        cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (255, 0, 0), 2)
        cv2.putText(frame, label, (int(x1), int(y1)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0), 2)

    # Afficher le cadre
    cv2.imshow('Detection', frame)

    # Quitter avec 'q'
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Libérer les ressources
cap.release()
cv2.destroyAllWindows()
