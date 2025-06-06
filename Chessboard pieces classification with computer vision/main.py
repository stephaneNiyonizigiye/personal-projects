import cv2
from ultralytics import YOLO

# Charger le modèle
model = YOLO('best.pt')

# Initialiser la capture vidéo
cap = cv2.VideoCapture(0)  # 0 pour la caméra par défaut

while True:
    ret, frame = cap.read()
    if not ret:
        break

    # Effectuer la détection
    results = model(frame)

    # Vérifiez la structure des résultats
    print(type(results))  # Cela devrait donner 'list'

    if isinstance(results, list):
        for result in results:
            if hasattr(result, 'boxes'):
                for box in result.boxes:
                    print("Box content:", box)  # Imprimez le contenu de la boîte pour voir sa structure

                    # Récupérer la classe et la confiance
                    cls = box.cls.item()  # Indice de la classe
                    conf = box.conf.item()  # Confiance

                    # Créer le label avec la classe et la confiance
                    label = f"{model.names[int(cls)]} {conf:.2f}"

                    # Vérifiez que 'xyxy' existe et récupérez les valeurs
                    if hasattr(box, 'xyxy') and box.xyxy.size(1) >= 4:  # Assurez-vous que le tenseur a au moins 4 valeurs
                        x1, y1, x2, y2 = box.xyxy[0].tolist()  # Convertir le tenseur en liste pour le déballage
                       
                        
                        # Calculer le centre de la boîte
                        x_center = (x1 + x2) / 2
                        y_center = (y1 + y2) / 2
                                            
                        #dessiner un petit cercle au centre
                        cv2.circle(frame, (int(x_center), int(y_center)), 5, (255, 0, 0), -1)
                        
                        # Dessiner la boîte sur l'image
                        cv2.rectangle(frame, (int(x1), int(y1)), (int(x2), int(y2)), (0, 255, 0), 2)
                        cv2.putText(frame, label, (int(x1), int(y1)-10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0,255), 2)
                    else:
                        print("La boîte ne contient pas suffisamment de valeurs pour les coordonnées.")
            else:
                print("Le résultat n'a pas d'attribut 'boxes'")

    # Afficher le cadre avec les détections
    cv2.imshow('Detection', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
