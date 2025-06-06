import cv2
import numpy as np

# Charger l'image
image = cv2.imread("echiquier-souple-taille-5.jpg")

# Conversion en niveaux de gris
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# Appliquer un flou gaussien
blurred = cv2.GaussianBlur(gray, (5, 5), 0)

# Détection des bords avec Canny
edges = cv2.Canny(blurred, 50, 150)

# Détection des contours
contours, _ = cv2.findContours(edges, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

# Trouver le plus grand rectangle
max_area = 0
best_contour = None

for contour in contours:
    area = cv2.contourArea(contour)
    if area > max_area:
        # Approximer le contour pour le simplifier
        perimeter = cv2.arcLength(contour, True)
        approx = cv2.approxPolyDP(contour, 0.02 * perimeter, True)

        # Vérifier si le contour est un rectangle
        if len(approx) == 4:  # Doit avoir 4 sommets
            max_area = area
            best_contour = approx

# Si un rectangle a été trouvé
if best_contour is not None:
    cv2.drawContours(image, [best_contour], -1, (0, 255, 0), 2)

    # Obtenir les coordonnées des coins
    pts = best_contour.reshape(4, 2)
    pts = np.array(pts, dtype='float32')

    # Définir les points de destination pour la transformation de perspective
    width, height = 400, 400  # Dimensions souhaitées pour l'échiquier
    dst_pts = np.array([[0, 0], [width, 0], [width, height], [0, height]], dtype='float32')

    # Calculer la matrice de transformation
    matrix = cv2.getPerspectiveTransform(pts, dst_pts)

    # Appliquer la transformation de perspective
    warped = cv2.warpPerspective(image, matrix, (width, height))

    # Diviser l'image redressée en 64 cases
    case_width = width // 8
    case_height = height // 8

    for i in range(8):
        for j in range(8):
            # Calculer les coordonnées de la case
            x1 = j * case_width
            y1 = i * case_height
            x2 = (j + 1) * case_width
            y2 = (i + 1) * case_height

            # Déterminer si la case est noire (en vérifiant la moyenne des pixels)
            roi = warped[y1:y2, x1:x2]
            mean_color = cv2.mean(roi)[:3]  # Récupérer la couleur moyenne (B, G, R)

            # Colorer les cases noires en vert
            if mean_color[0] < 100 and mean_color[1] < 100 and mean_color[2] < 100:  # Vérifie si c'est sombre
                warped[y1:y2, x1:x2] = (0, 255, 0)  # Remplacer par du vert

    # Afficher l'image redressée avec les cases colorées
    cv2.imshow("Warped Chessboard with Colored Squares", warped)
else:
    print("Aucun rectangle n'a été trouvé.")

# Afficher l'image d'origine avec le contour détecté
cv2.imshow("Detected Rectangle", image)
cv2.waitKey(0)
cv2.destroyAllWindows()
