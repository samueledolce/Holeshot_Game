#!/usr/bin/env python3
"""
Genera gli sfondi di Holeshot.

Gli sfondi sono disegnati interamente da questo script (forme geometriche e
sfumature): non derivano da fotografie o immagini di terzi, quindi possono
stare senza problemi di licenza in un repository pubblico.

Uso (servono Python 3, Pillow e NumPy):

    python3 strumenti/genera_sfondi.py

Le immagini vengono scritte tra le risorse dell'applicazione, nella cartella
src/main/resources/it/unicam/cs/mpgc/rpg129875/grafica/.
"""
import math
import random
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw, ImageFilter

LARGHEZZA, ALTEZZA = 1280, 800
SCALA = 2  # si disegna al doppio e si rimpicciolisce: bordi più morbidi
W, H = LARGHEZZA * SCALA, ALTEZZA * SCALA
CARTELLA = Path(__file__).resolve().parent.parent / "src/main/resources/it/unicam/cs/mpgc/rpg129875/grafica"


def colore(esadecimale, alfa=255):
    esadecimale = esadecimale.lstrip("#")
    return tuple(int(esadecimale[i:i + 2], 16) for i in (0, 2, 4)) + (alfa,)


def sfumatura_verticale(fermate):
    """fermate: elenco di (posizione tra 0 e 1, colore esadecimale), dall'alto verso il basso."""
    ys = np.linspace(0, 1, H)
    posizioni = [p for p, _ in fermate]
    canali = []
    for canale in range(3):
        valori = [colore(c)[canale] for _, c in fermate]
        canali.append(np.interp(ys, posizioni, valori))
    colonna = np.stack(canali, axis=1).astype(np.uint8)
    matrice = np.repeat(colonna[:, None, :], W, axis=1)
    return Image.fromarray(matrice, "RGB").convert("RGBA")


def strato():
    return Image.new("RGBA", (W, H), (0, 0, 0, 0))


def profilo_colline(seme, base, ampiezza, onde):
    """Linea dell'orizzonte ondulata: somma di sinusoidi con fasi casuali."""
    generatore = random.Random(seme)
    fasi = [(generatore.uniform(0, math.tau), generatore.uniform(0.6, 1.4)) for _ in onde]
    punti = []
    for x in range(0, W + 8, 8):
        y = base
        for (lunghezza, peso), (fase, variazione) in zip(onde, fasi):
            y += math.sin(x / (lunghezza * SCALA) * variazione + fase) * ampiezza * peso * SCALA
        punti.append((x, y))
    return punti


def disegna_colline(immagine, seme, base, ampiezza, tinta, onde=((260, 1.0), (97, 0.45), (41, 0.18))):
    livello = strato()
    punti = profilo_colline(seme, base * SCALA, ampiezza, onde)
    ImageDraw.Draw(livello).polygon(punti + [(W, H), (0, H)], fill=colore(tinta))
    return Image.alpha_composite(immagine, livello)


def alone(immagine, centro, raggio, tinta, intensita):
    """Bagliore circolare morbido (es. il sole)."""
    cx, cy = centro[0] * SCALA, centro[1] * SCALA
    ys, xs = np.mgrid[0:H, 0:W]
    distanza = np.sqrt((xs - cx) ** 2 + (ys - cy) ** 2) / (raggio * SCALA)
    alfa = np.clip(1 - distanza, 0, 1) ** 2 * intensita
    r, g, b, _ = colore(tinta)
    livello = np.zeros((H, W, 4), dtype=np.uint8)
    livello[..., 0], livello[..., 1], livello[..., 2] = r, g, b
    livello[..., 3] = (alfa * 255).astype(np.uint8)
    return Image.alpha_composite(immagine, Image.fromarray(livello, "RGBA"))


def salva(immagine, nome):
    CARTELLA.mkdir(parents=True, exist_ok=True)
    finale = immagine.convert("RGB").resize((LARGHEZZA, ALTEZZA), Image.LANCZOS)
    percorso = CARTELLA / nome
    finale.save(percorso, quality=88, optimize=True)
    print(f"{nome}: {percorso.stat().st_size // 1024} KB")


# --------------------------------------------------------------------------
# Pilota in salto (sagoma)
# --------------------------------------------------------------------------

def sagoma_pilota(disegno, origine, dimensione, inclinazione_gradi, tinta):
    """Sagoma di moto e pilota. origine = asse della ruota posteriore; dimensione = interasse in pixel."""
    angolo = math.radians(inclinazione_gradi)
    cos_a, sin_a = math.cos(angolo), math.sin(angolo)
    ox, oy = origine[0] * SCALA, origine[1] * SCALA
    unita = dimensione * SCALA / 100.0
    pieno = colore(tinta)

    def p(x, y):
        return (ox + (x * cos_a - y * sin_a) * unita, oy + (x * sin_a + y * cos_a) * unita)

    def poligono(*punti):
        disegno.polygon([p(x, y) for x, y in punti], fill=pieno)

    def tratto(a, b, spessore):
        larghezza = max(1, int(spessore * unita))
        disegno.line([p(*a), p(*b)], fill=pieno, width=larghezza)
        for punto in (a, b):
            cx, cy = p(*punto)
            raggio = larghezza / 2
            disegno.ellipse([cx - raggio, cy - raggio, cx + raggio, cy + raggio], fill=pieno)

    def ruota(centro, raggio):
        cx, cy = p(*centro)
        esterno = raggio * unita
        disegno.ellipse([cx - esterno, cy - esterno, cx + esterno, cy + esterno],
                        outline=pieno, width=max(2, int(6.5 * unita)))
        mozzo = 4 * unita
        disegno.ellipse([cx - mozzo, cy - mozzo, cx + mozzo, cy + mozzo], fill=pieno)
        for raggio_gradi in range(0, 180, 45):
            dx, dy = math.cos(math.radians(raggio_gradi)) * esterno, math.sin(math.radians(raggio_gradi)) * esterno
            disegno.line([cx - dx, cy - dy, cx + dx, cy + dy], fill=pieno, width=max(1, int(1.2 * unita)))

    posteriore, anteriore = (0, 0), (104, 4)
    ruota(posteriore, 25)
    ruota(anteriore, 26)
    # forcellone, telaio e motore
    tratto(posteriore, (44, -14), 7)
    poligono((34, -6), (40, -34), (70, -40), (76, -12), (60, 4), (42, 4))
    # sella e codone rialzato
    poligono((-12, -46), (6, -50), (44, -42), (40, -32), (8, -36))
    # parafango posteriore
    poligono((-30, -52), (-10, -50), (-6, -44), (-26, -44))
    # serbatoio e convogliatori
    poligono((40, -44), (66, -54), (80, -42), (72, -30), (44, -32))
    # forcella e manubrio
    tratto(anteriore, (78, -62), 6)
    tratto((78, -62), (70, -68), 5)
    # parafango anteriore
    poligono((84, -34), (120, -30), (124, -24), (88, -26))
    # tabella portanumero
    poligono((76, -70), (88, -64), (86, -48), (76, -52))
    # pilota in piedi sulle pedane, busto in avanti
    tratto((46, -8), (38, -44), 11)      # stinco
    tratto((38, -44), (20, -66), 13)     # coscia
    tratto((20, -66), (52, -92), 17)     # busto
    tratto((50, -90), (62, -76), 8)      # braccio
    tratto((62, -76), (72, -68), 7)      # avambraccio
    testa_x, testa_y = p(60, -106)
    raggio_testa = 12 * unita
    disegno.ellipse([testa_x - raggio_testa, testa_y - raggio_testa,
                     testa_x + raggio_testa, testa_y + raggio_testa], fill=pieno)
    poligono((66, -112), (82, -108), (70, -102))   # visiera del casco
    poligono((62, -100), (76, -96), (66, -92))     # mentoniera


# --------------------------------------------------------------------------
# Sfondo del menu: tramonto con salto
# --------------------------------------------------------------------------

def sfondo_menu():
    """Tramonto: a destra il pilota salta davanti al sole, a sinistra resta spazio per il menu."""
    immagine = sfumatura_verticale([
        (0.00, "#10162b"), (0.30, "#2b2350"), (0.52, "#7a3b63"),
        (0.66, "#e0703f"), (0.74, "#f9b55a"), (1.00, "#f9b55a"),
    ])
    disegno = ImageDraw.Draw(immagine)
    generatore = random.Random(7)
    for _ in range(140):  # stelle, solo nella parte alta del cielo
        x, y = generatore.uniform(0, W), generatore.uniform(0, H * 0.34)
        raggio = generatore.choice([1, 1, 1.5, 2]) * SCALA / 2
        luminosita = int(generatore.uniform(60, 190) * (1 - y / (H * 0.34)))
        disegno.ellipse([x - raggio, y - raggio, x + raggio, y + raggio], fill=(255, 244, 225, luminosita))

    sole = (905, 500)
    immagine = alone(immagine, sole, 560, "#ffd27a", 0.55)
    immagine = alone(immagine, sole, 250, "#fff0c0", 0.75)
    disegno = ImageDraw.Draw(immagine)
    raggio_sole = 128 * SCALA
    disegno.ellipse([sole[0] * SCALA - raggio_sole, sole[1] * SCALA - raggio_sole,
                     sole[0] * SCALA + raggio_sole, sole[1] * SCALA + raggio_sole], fill=colore("#fff3c9"))

    immagine = disegna_colline(immagine, 11, 572, 30, "#8a4467", onde=((330, 1.0), (120, 0.5), (55, 0.2)))
    immagine = disegna_colline(immagine, 23, 618, 26, "#5a2f55", onde=((240, 1.0), (90, 0.5), (38, 0.2)))
    immagine = disegna_colline(immagine, 37, 672, 18, "#33203f", onde=((200, 1.0), (70, 0.45), (30, 0.15)))

    # primo piano: terreno scuro con la rampa di un salto sulla destra
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    terreno = "#15101c"
    profilo = [(0, 722), (180, 734), (420, 748), (560, 742), (650, 706), (730, 648), (786, 606), (810, 604),
               (830, 648), (870, 722), (960, 748), (1100, 752), (1280, 740)]
    disegno.polygon([(x * SCALA, y * SCALA) for x, y in profilo] + [(W, H), (0, H)], fill=colore(terreno))
    # paletti e fettuccia lungo la pista
    paletti = [(70, 724), (190, 732), (310, 740), (430, 746), (540, 742)]
    for x, y in paletti:
        disegno.rectangle([x * SCALA, (y - 30) * SCALA, (x + 4) * SCALA, y * SCALA], fill=colore(terreno))
    disegno.line([((x + 2) * SCALA, (y - 24) * SCALA) for x, y in paletti], fill=colore(terreno), width=2 * SCALA)
    asse_posteriore = (838, 468)
    sagoma_pilota(disegno, asse_posteriore, 122, -24, terreno)
    # zolle di terra sollevate dalla ruota posteriore
    for dx, dy, raggio in [(-28, 40, 5), (-46, 62, 4), (-18, 70, 3), (-62, 92, 3), (-36, 104, 2), (-8, 96, 2)]:
        cx, cy = (asse_posteriore[0] + dx) * SCALA, (asse_posteriore[1] + dy) * SCALA
        disegno.ellipse([cx - raggio * SCALA, cy - raggio * SCALA, cx + raggio * SCALA, cy + raggio * SCALA],
                        fill=colore(terreno))
    immagine = Image.alpha_composite(immagine, livello)
    salva(immagine, "sfondo-menu.jpg")


# --------------------------------------------------------------------------
# Sfondo del box: grafica scura con bande da gara e battistrada
# --------------------------------------------------------------------------

def sfondo_box():
    immagine = sfumatura_verticale([(0.0, "#1d232e"), (0.55, "#141922"), (1.0, "#0c0f15")])
    immagine = alone(immagine, (300, 160), 700, "#3a4a66", 0.35)

    # battistrada: file di tasselli inclinati, appena visibili
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    passo_x, passo_y = 58 * SCALA, 46 * SCALA
    for riga in range(-2, H // passo_y + 3):
        for colonna in range(-2, W // passo_x + 3):
            x = colonna * passo_x + (riga % 2) * passo_x // 2
            y = riga * passo_y
            tassello = [(x, y), (x + 30 * SCALA, y - 10 * SCALA), (x + 34 * SCALA, y + 8 * SCALA), (x + 4 * SCALA, y + 18 * SCALA)]
            disegno.polygon(tassello, fill=(255, 255, 255, 7))
    immagine = Image.alpha_composite(immagine, livello)

    # bande diagonali nell'angolo in basso a destra
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    bande = [(0, 92, "#f5a623", 235), (104, 26, "#ffffff", 200), (142, 150, "#c2410c", 200), (304, 14, "#ffffff", 90)]
    for inizio, spessore, tinta, alfa in bande:
        a = (W - (560 - inizio) * SCALA, H)
        b = (W - (560 - inizio - spessore) * SCALA, H)
        c = (W, H - (560 - inizio - spessore) * SCALA * 0.82)
        d = (W, H - (560 - inizio) * SCALA * 0.82)
        disegno.polygon([a, b, c, d], fill=colore(tinta, alfa))
    immagine = Image.alpha_composite(immagine, livello)

    # vignettatura leggera per tenere lo sguardo al centro
    ys, xs = np.mgrid[0:H, 0:W]
    distanza = np.sqrt(((xs - W / 2) / (W / 2)) ** 2 + ((ys - H / 2) / (H / 2)) ** 2)
    ombra = np.zeros((H, W, 4), dtype=np.uint8)
    ombra[..., 3] = (np.clip(distanza - 0.75, 0, 1) * 150).astype(np.uint8)
    immagine = Image.alpha_composite(immagine, Image.fromarray(ombra, "RGBA"))
    salva(immagine, "sfondo-box.jpg")


# --------------------------------------------------------------------------
# Sfondi delle piste: il rettilineo di partenza visto dal cancelletto
# --------------------------------------------------------------------------

def sfondo_pista(nome, cielo, colline, terra_chiara, terra_scura, solchi, bordi, extra=None, foschia=None):
    immagine = sfumatura_verticale(cielo)
    orizzonte = 372
    if foschia:
        immagine = alone(immagine, (640, orizzonte), 760, foschia, 0.55)
    for indice, (base, ampiezza, tinta) in enumerate(colline):
        immagine = disegna_colline(immagine, 50 + indice * 13, base, ampiezza, tinta)

    # il terreno: sfumatura che si scurisce verso l'osservatore
    terreno = sfumatura_verticale([(0.0, terra_chiara), (orizzonte / ALTEZZA, terra_chiara), (1.0, terra_scura)])
    maschera = Image.new("L", (W, H), 0)
    ImageDraw.Draw(maschera).rectangle([0, orizzonte * SCALA, W, H], fill=255)
    immagine.paste(terreno, (0, 0), maschera)

    generatore = random.Random(len(nome))
    fuga = (640 * SCALA, (orizzonte - 6) * SCALA)

    def bordo_pista(lato, t):
        """Punto sul bordo della pista: t = 0 al punto di fuga, cresce verso l'osservatore."""
        return fuga[0] + lato * (70 + t * 1260) * SCALA, fuga[1] + (10 + t * 300) * SCALA

    # granelli e zolle: rompono l'uniformità del terreno, più grandi vicino all'osservatore
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    for _ in range(900):
        y = generatore.uniform(orizzonte + 8, ALTEZZA)
        profondita = (y - orizzonte) / (ALTEZZA - orizzonte)
        x = generatore.uniform(0, LARGHEZZA)
        raggio_x, raggio_y = (1 + 7 * profondita) * SCALA, (0.5 + 2.2 * profondita) * SCALA
        tono = generatore.choice([(0, 0, 0, 26), (0, 0, 0, 18), (255, 255, 255, 16)])
        disegno.ellipse([x * SCALA - raggio_x, y * SCALA - raggio_y, x * SCALA + raggio_x, y * SCALA + raggio_y], fill=tono)
    immagine = Image.alpha_composite(immagine, livello)

    livello = strato()
    disegno = ImageDraw.Draw(livello)
    # la pista: un trapezio che si stringe verso il punto di fuga, appena più chiaro del terreno
    r, g, b, _ = colore(terra_chiara)
    disegno.polygon([(-260 * SCALA, H), (W + 260 * SCALA, H), (fuga[0] + 46 * SCALA, fuga[1] + 8 * SCALA),
                     (fuga[0] - 46 * SCALA, fuga[1] + 8 * SCALA)], fill=(r, g, b, 70))

    # solchi lasciati dalle moto: diretti al punto di fuga, ognuno un po' diverso dagli altri
    for posizione in range(-4, 5):
        base_x = (640 + posizione * 176 + generatore.uniform(-26, 26)) * SCALA
        semi_larghezza = generatore.uniform(10, 21) * SCALA
        arrivo = fuga[0] + posizione * 8 * SCALA
        disegno.polygon([(base_x - semi_larghezza, H), (base_x + semi_larghezza, H),
                         (arrivo + SCALA, fuga[1] + 10 * SCALA), (arrivo - SCALA, fuga[1] + 10 * SCALA)],
                        fill=colore(solchi, generatore.randint(70, 120)))
    immagine = Image.alpha_composite(immagine, livello.filter(ImageFilter.GaussianBlur(0.8 * SCALA)))

    livello = strato()
    disegno = ImageDraw.Draw(livello)
    # arco gonfiabile in lontananza, a cavallo del rettilineo
    t_arco = 0.085
    sinistra, destra = bordo_pista(-1, t_arco), bordo_pista(1, t_arco)
    altezza_arco, spessore = 74 * SCALA, 15 * SCALA
    for x, y in (sinistra, destra):
        disegno.rectangle([x - spessore / 2, y - altezza_arco, x + spessore / 2, y], fill=colore(bordi[0]))
    disegno.rectangle([sinistra[0] - spessore / 2, sinistra[1] - altezza_arco - spessore,
                       destra[0] + spessore / 2, sinistra[1] - altezza_arco + spessore * 0.4], fill=colore(bordi[0]))
    larghezza_arco = destra[0] - sinistra[0]
    for frazione in (0.2, 0.4, 0.6, 0.8):  # tasselli chiari sulla traversa
        cx = sinistra[0] + larghezza_arco * frazione
        disegno.rectangle([cx - 14 * SCALA, sinistra[1] - altezza_arco - spessore * 0.62,
                           cx + 14 * SCALA, sinistra[1] - altezza_arco + spessore * 0.02], fill=colore(bordi[1]))

    # striscioni ai due lati, in prospettiva: più vicini sono, più sono grandi
    tratti = [(0.012, 0.050), (0.060, 0.115), (0.135, 0.215), (0.245, 0.365), (0.405, 0.600)]
    for lato in (-1, 1):
        for indice, (t0, t1) in enumerate(tratti):
            tinta = bordi[indice % len(bordi)]
            punti = []
            for t, alto in ((t0, False), (t1, False), (t1, True), (t0, True)):
                x, y_base = bordo_pista(lato, t)
                altezza = (5 + t * 150) * SCALA
                punti.append((x, y_base - altezza if alto else y_base))
            disegno.polygon(punti, fill=colore(tinta, 240))
            for t in (t0, t1):  # paletti di sostegno
                x, y_base = bordo_pista(lato, t)
                altezza = (5 + t * 150) * SCALA * 1.22
                semi_spessore = (1 + t * 9) * SCALA
                disegno.rectangle([x - semi_spessore, y_base - altezza, x + semi_spessore, y_base + 3 * SCALA],
                                  fill=colore("#2a2420"))
    immagine = Image.alpha_composite(immagine, livello)

    if extra:
        immagine = extra(immagine, orizzonte)

    # ombra in basso: lì si trova il cancelletto, e il testo deve restare leggibile
    ys = np.linspace(0, 1, H)
    alfa = (np.clip((ys - 0.62) / 0.38, 0, 1) ** 1.6 * 150).astype(np.uint8)
    ombra = np.zeros((H, W, 4), dtype=np.uint8)
    ombra[..., 3] = alfa[:, None]
    immagine = Image.alpha_composite(immagine, Image.fromarray(ombra, "RGBA"))
    salva(immagine, nome)


def pozzanghere_e_pioggia(immagine, orizzonte):
    livello = Image.new("RGBA", (W, H), (142, 155, 167, 0))
    disegno = ImageDraw.Draw(livello)
    for cx, cy, larghezza in [(420, 690, 230), (900, 610, 170), (700, 520, 110), (300, 500, 90), (1040, 730, 200), (590, 440, 60)]:
        altezza = larghezza * 0.16
        disegno.ellipse([(cx - larghezza / 2) * SCALA, (cy - altezza / 2) * SCALA,
                         (cx + larghezza / 2) * SCALA, (cy + altezza / 2) * SCALA], fill=colore("#8e9ba7", 140))
        disegno.ellipse([(cx - larghezza * 0.30) * SCALA, (cy - altezza * 0.30) * SCALA,
                         (cx + larghezza * 0.12) * SCALA, (cy + altezza * 0.02) * SCALA], fill=colore("#c9d3db", 120))
    immagine = Image.alpha_composite(immagine, livello.filter(ImageFilter.GaussianBlur(2.5 * SCALA)))
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    generatore = random.Random(5)
    for _ in range(420):
        x, y = generatore.uniform(-100, W), generatore.uniform(0, H)
        lunghezza = generatore.uniform(16, 34) * SCALA
        disegno.line([x, y, x + lunghezza * 0.22, y + lunghezza], fill=(225, 232, 240, generatore.randint(28, 62)), width=SCALA)
    return Image.alpha_composite(immagine, livello)


def increspature_di_sabbia(immagine, orizzonte):
    livello = strato()
    disegno = ImageDraw.Draw(livello)
    generatore = random.Random(9)
    for _ in range(46):
        y = generatore.uniform(orizzonte + 30, ALTEZZA)
        profondita = (y - orizzonte) / (ALTEZZA - orizzonte)
        larghezza = generatore.uniform(60, 220) * (0.3 + profondita)
        x = generatore.uniform(0, LARGHEZZA)
        disegno.arc([(x - larghezza / 2) * SCALA, (y - 5 * profondita) * SCALA,
                     (x + larghezza / 2) * SCALA, (y + 9 * profondita) * SCALA],
                    start=200, end=340, fill=colore("#a8824a", 80), width=max(1, int(2 * SCALA * profondita)))
    return Image.alpha_composite(immagine, livello)


def nuvole(immagine, orizzonte):
    livello = Image.new("RGBA", (W, H), (255, 255, 255, 0))  # base bianca trasparente: niente alone scuro sfocando
    disegno = ImageDraw.Draw(livello)
    for cx, cy, larghezza, altezza in [(250, 150, 300, 46), (420, 180, 220, 34), (930, 110, 360, 52), (1090, 150, 220, 34), (680, 230, 200, 26)]:
        disegno.ellipse([(cx - larghezza / 2) * SCALA, (cy - altezza / 2) * SCALA,
                         (cx + larghezza / 2) * SCALA, (cy + altezza / 2) * SCALA], fill=(255, 255, 255, 190))
    return Image.alpha_composite(immagine, livello.filter(ImageFilter.GaussianBlur(11 * SCALA)))


def sfondi_piste():
    sfondo_pista(
        "sfondo-pista-asciutta.jpg",
        cielo=[(0.0, "#2f7fd6"), (0.30, "#7ab8ee"), (0.47, "#d6ebfa"), (1.0, "#d6ebfa")],
        colline=[(338, 22, "#6f9a78"), (356, 14, "#4f7d57"), (368, 8, "#3d6646")],
        terra_chiara="#c7935c", terra_scura="#7a4d2b", solchi="#5f3a1f",
        bordi=["#f5a623", "#ffffff", "#c2410c", "#f5a623"],
        extra=nuvole, foschia="#fff4dc")
    sfondo_pista(
        "sfondo-pista-fangosa.jpg",
        cielo=[(0.0, "#3f4a55"), (0.30, "#66727d"), (0.47, "#99a3ab"), (1.0, "#99a3ab")],
        colline=[(336, 20, "#55635b"), (354, 14, "#3f4d45"), (368, 8, "#2f3b35")],
        terra_chiara="#6b4a36", terra_scura="#2f1f17", solchi="#1f140f",
        bordi=["#d99117", "#d8d8d8", "#a8360a", "#d99117"],
        extra=pozzanghere_e_pioggia, foschia="#b9c2c9")
    sfondo_pista(
        "sfondo-pista-sabbiosa.jpg",
        cielo=[(0.0, "#3aa0e0"), (0.28, "#8fd0f5"), (0.47, "#fdf0cf"), (1.0, "#fdf0cf")],
        colline=[(340, 20, "#e6cc92"), (356, 16, "#d9b876"), (368, 9, "#c9a45f")],
        terra_chiara="#ecd59c", terra_scura="#b08a4c", solchi="#9a7440",
        bordi=["#f5a623", "#ffffff", "#c2410c", "#f5a623"],
        extra=increspature_di_sabbia, foschia="#fffbe8")


if __name__ == "__main__":
    sfondo_menu()
    sfondo_box()
    sfondi_piste()
