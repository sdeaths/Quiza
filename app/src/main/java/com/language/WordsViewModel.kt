package com.language

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class WordsViewModel {
    private val db = FirebaseFirestore.getInstance()

    // Метод для загрузки наборов слов
    fun uploadWordSets() {
        val wordSets = mapOf(
            "English" to mapOf(
                "Nature" to mapOf(
                    "Tree" to "дерево",
                    "River" to "река",
                    "Mountain" to "гора",
                    "Sky" to "небо",
                    "Ocean" to "океан",
                    "Flower" to "цветок",
                    "Grass" to "трава",
                    "Leaf" to "лист",
                    "Stone" to "камень",
                    "Sunset" to "закат"
                ),
                "Food" to mapOf(
                    "Bread" to "хлеб",
                    "Cheese" to "сыр",
                    "Meat" to "мясо",
                    "Fruit" to "фрукт",
                    "Vegetable" to "овощ",
                    "Rice" to "рис",
                    "Soup" to "суп",
                    "Sugar" to "сахар",
                    "Salt" to "соль",
                    "Juice" to "сок"
                ),
                "Emotions" to mapOf(
                    "Happy" to "счастливый",
                    "Sad" to "грустный",
                    "Angry" to "злой",
                    "Excited" to "взволнованный",
                    "Scared" to "испуганный",
                    "Tired" to "уставший",
                    "Nervous" to "нервный",
                    "Confident" to "уверенный",
                    "Bored" to "скучающий",
                    "Surprised" to "удивленный"
                ),
                "Travel" to mapOf(
                    "Ticket" to "билет",
                    "Passport" to "паспорт",
                    "Airport" to "аэропорт",
                    "Luggage" to "багаж",
                    "Hotel" to "отель",
                    "Map" to "карта",
                    "Train" to "поезд",
                    "Flight" to "рейс",
                    "Guide" to "гид",
                    "Souvenir" to "сувенир"
                ),
                "Technology" to mapOf(
                    "Computer" to "компьютер",
                    "Smartphone" to "смартфон",
                    "Internet" to "интернет",
                    "Software" to "программное обеспечение",
                    "Keyboard" to "клавиатура",
                    "Screen" to "экран",
                    "Charger" to "зарядное устройство",
                    "Headphones" to "наушники",
                    "Battery" to "батарея",
                    "Laptop" to "ноутбук"
                ),
                "Jobs" to mapOf(
                    "Doctor" to "врач",
                    "Teacher" to "учитель",
                    "Engineer" to "инженер",
                    "Lawyer" to "юрист",
                    "Chef" to "повар",
                    "Artist" to "художник",
                    "Driver" to "водитель",
                    "Scientist" to "ученый",
                    "Actor" to "актер",
                    "Writer" to "писатель"
                )
            ),
            "German" to mapOf(
                "Natur" to mapOf(
                    "Baum" to "дерево",
                    "Fluss" to "река",
                    "Berg" to "гора",
                    "Himmel" to "небо",
                    "Ozean" to "океан",
                    "Blume" to "цветок",
                    "Gras" to "трава",
                    "Blatt" to "лист",
                    "Stein" to "камень",
                    "Sonnenuntergang" to "закат"
                ),
                "Essen" to mapOf(
                    "Brot" to "хлеб",
                    "Käse" to "сыр",
                    "Fleisch" to "мясо",
                    "Obst" to "фрукт",
                    "Gemüse" to "овощ",
                    "Reis" to "рис",
                    "Suppe" to "суп",
                    "Zucker" to "сахар",
                    "Salz" to "соль",
                    "Saft" to "сок"
                ),
                "Emotionen" to mapOf(
                    "Glücklich" to "счастливый",
                    "Traurig" to "грустный",
                    "Wütend" to "злой",
                    "Aufgeregt" to "взволнованный",
                    "Ängstlich" to "испуганный",
                    "Müde" to "уставший",
                    "Nervös" to "нервный",
                    "Selbstbewusst" to "уверенный",
                    "Gelangweilt" to "скучающий",
                    "Überrascht" to "удивленный"
                ),
                "Reisen" to mapOf(
                    "Ticket" to "билет",
                    "Reisepass" to "паспорт",
                    "Flughafen" to "аэропорт",
                    "Gepäck" to "багаж",
                    "Hotel" to "отель",
                    "Karte" to "карта",
                    "Zug" to "поезд",
                    "Flug" to "рейс",
                    "Reiseführer" to "гид",
                    "Souvenir" to "сувенир"
                ),
                "Technologie" to mapOf(
                    "Computer" to "компьютер",
                    "Smartphone" to "смартфон",
                    "Internet" to "интернет",
                    "Software" to "программное обеспечение",
                    "Tastatur" to "клавиатура",
                    "Bildschirm" to "экран",
                    "Ladegerät" to "зарядное устройство",
                    "Kopfhörer" to "наушники",
                    "Batterie" to "батарея",
                    "Laptop" to "ноутбук"
                ),
                "Berufe" to mapOf(
                    "Arzt" to "врач",
                    "Lehrer" to "учитель",
                    "Ingenieur" to "инженер",
                    "Anwalt" to "юрист",
                    "Koch" to "повар",
                    "Künstler" to "художник",
                    "Fahrer" to "водитель",
                    "Wissenschaftler" to "ученый",
                    "Schauspieler" to "актер",
                    "Schriftsteller" to "писатель"
                )
            ),
            "French" to mapOf(
                "Nature" to mapOf(
                    "Arbre" to "дерево",
                    "Rivière" to "река",
                    "Montagne" to "гора",
                    "Ciel" to "небо",
                    "Océan" to "океан",
                    "Fleur" to "цветок",
                    "Herbe" to "трава",
                    "Feuille" to "лист",
                    "Pierre" to "камень",
                    "Coucher de soleil" to "закат"
                ),
                "Nourriture" to mapOf(
                    "Pain" to "хлеб",
                    "Fromage" to "сыр",
                    "Viande" to "мясо",
                    "Fruit" to "фрукт",
                    "Légume" to "овощ",
                    "Riz" to "рис",
                    "Soupe" to "суп",
                    "Sucre" to "сахар",
                    "Sel" to "соль",
                    "Jus" to "сок"
                ),
                "Émotions" to mapOf(
                    "Heureux" to "счастливый",
                    "Triste" to "грустный",
                    "En colère" to "злой",
                    "Excité" to "взволнованный",
                    "Effrayé" to "испуганный",
                    "Fatigué" to "уставший",
                    "Nerveux" to "нервный",
                    "Confiant" to "уверенный",
                    "Ennuyé" to "скучающий",
                    "Surpris" to "удивленный"
                ),
                "Voyage" to mapOf(
                    "Billet" to "билет",
                    "Passeport" to "паспорт",
                    "Aéroport" to "аэропорт",
                    "Bagage" to "багаж",
                    "Hôtel" to "отель",
                    "Carte" to "карта",
                    "Train" to "поезд",
                    "Vol" to "рейс",
                    "Guide" to "гид",
                    "Souvenir" to "сувенир"
                ),
                "Technologie" to mapOf(
                    "Ordinateur" to "компьютер",
                    "Smartphone" to "смартфон",
                    "Internet" to "интернет",
                    "Logiciel" to "программное обеспечение",
                    "Clavier" to "клавиатура",
                    "Écran" to "экран",
                    "Chargeur" to "зарядное устройство",
                    "Écouteurs" to "наушники",
                    "Batterie" to "батарея",
                    "Ordinateur portatif" to "ноутбук"
                ),
                "Métiers" to mapOf(
                    "Médecin" to "врач",
                    "Professeur" to "учитель",
                    "Ingénieur" to "инженер",
                    "Avocat" to "юрист",
                    "Cuisinier" to "повар",
                    "Artiste" to "художник",
                    "Chauffeur" to "водитель",
                    "Scientifique" to "ученый",
                    "Acteur" to "актер",
                    "Écrivain" to "писатель"
                )
            )
        )

        Log.d("Firestore", "🚀 Начинаем загрузку данных в Firestore...")

        val batch = db.batch()

        for ((language, categories) in wordSets) {
            val languageRef = db.collection("languages").document(language)
            Log.d("Firestore", "📌 Добавляю язык: $language")

            categories.forEach { (category, words) ->
                val categoryRef = languageRef.collection("categories").document(category)

                // ✅ Добавляем категорию как документ в Firestore
                val categoryData = hashMapOf(
                    "name" to category,
                    "createdAt" to System.currentTimeMillis()
                )
                batch.set(categoryRef, categoryData)

                Log.d("Firestore", "  ├── Категория: $category")

                words.forEach { (word, translation) ->
                    val wordRef = categoryRef.collection("words").document(word)
                    val wordData = hashMapOf(
                        "original" to word,
                        "translation" to translation,
                        "createdAt" to System.currentTimeMillis()
                    )

                    Log.d("Firestore", "    ├── Слово: $word -> $translation")
                    batch.set(wordRef, wordData)
                }
            }
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Все данные успешно загружены!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Ошибка при загрузке данных: ${e.message}", e)
            }
    }

    // Метод для создания MySet при первом запуске, если его нет
    fun createMySetIfNotExists(selectedLanguage: String) {
        val mySetRef = db.collection("languages")
            .document(selectedLanguage)
            .collection("categories")
            .document("MySet")

        mySetRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                val mySetData = hashMapOf(
                    "name" to "MySet",
                    "createdAt" to System.currentTimeMillis()
                )
                mySetRef.set(mySetData)
                    .addOnSuccessListener {
                        Log.d("Firestore", "✅ MySet создан для языка $selectedLanguage")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "❌ Ошибка при создании MySet: ${e.message}", e)
                    }
            } else {
                Log.d("Firestore", "📦 MySet уже существует для $selectedLanguage")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "❌ Ошибка при проверке MySet: ${e.message}", e)
        }
    }

    // Метод для добавления слова в MySet
    fun addWordToMySet(selectedLanguage: String, word: String, translation: String) {
        val mySetRef = db.collection("languages")
            .document(selectedLanguage)
            .collection("categories")
            .document("MySet")
            .collection("words")
            .document(word) // Используем слово как идентификатор

        val wordData = hashMapOf(
            "original" to word,
            "translation" to translation,
            "addedAt" to System.currentTimeMillis()
        )

        mySetRef.set(wordData)
            .addOnSuccessListener {
                Log.d("Firestore", "✅ Слово $word добавлено в MySet для языка $selectedLanguage")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "❌ Ошибка при добавлении слова в MySet: ${e.message}", e)
            }
    }
}