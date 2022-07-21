import kotlin.random.Random

fun main() {

        do {
                print("Choose an action (reset, shuffle, get, exit): \n> ")
                var command = readLine()!!
                when(command) {
                        "reset" -> Deck.reset()
                        "shuffle" -> Deck.shuffle()
                        "get" -> Deck.get()
                        "print" -> Deck.printCardList(Deck.cards)
                        "exit" -> break
                        else -> println("Wrong action.")
                }
        } while(command != "exit")
        println("Bye")

}



object Deck {

        var cards = mutableListOf<Card>()

        init {
                initialize()
        }

        fun initialize() {
                for(suit in SUITS.values())
                        for(rank in RANKS.values())
                                cards.add(Card(suit, rank))
        }

        fun reset() {
                cards = mutableListOf<Card>()
                initialize()
                println("Card deck is reset.")
        }

        fun shuffle() {
                var rnd = Random
                repeat(52) {
                        var cardIndex1 = rnd.nextInt(0,52)
                        var cardIndex2 = rnd.nextInt(0,52)
                        var swap = cards[cardIndex1]
                        cards[cardIndex1] =  cards[cardIndex2]
                        cards[cardIndex2] =  swap
                }
                println("Card deck is shuffled.")
        }

        fun get() {
                var rnd = Random
                print("Number of cards: \n> ")
                var numberOfCardsToGet = 0
                try {
                        numberOfCardsToGet = readLine()!!.toInt()
                } catch (e:Exception) {
                        println("Invalid number of cards.")
                        return
                }

                var selectedCards = mutableListOf<Card>()
                if(numberOfCardsToGet in 1..52) {
                        if(numberOfCardsToGet <= cards.count()) {
                                while(selectedCards.count() < numberOfCardsToGet) {
                                        var index = rnd.nextInt(0,cards.count())
                                        selectedCards.add(cards[index])
                                        cards.removeAt(index)
                                }
                        }
                        else
                                println("The remaining cards are insufficient to meet the request.")
                }
                else
                        println("Invalid number of cards.")

                printCardList(selectedCards)
        }

        fun printCardList(cardListToPrint:MutableList<Card>) {
                for(card in cardListToPrint)
                        card.print()
                println()
        }
}

class Card(var suit:SUITS, var rank:RANKS) {

        fun print() {
                print("${rank.Symbol}${suit.Symbol} ")
        }
}

enum class RANKS(val Symbol:String, Rank: Int) {
        ACE("A", 1),
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
}

enum class SUITS(val CodePoint: Char, val Symbol:String, Rank: Int) {
        CLUB('\u2663', Character.toString('\u2663'), 0),
        SPADE('\u2660', Character.toString('\u2660'), 1),
        HEART('\u2665', Character.toString('\u2665'), 2),
        DIAMOND('\u2666', Character.toString('\u2666'), 3)
}

fun stage1() {
        // https://en.wikipedia.org/wiki/Playing_cards_in_Unicode

        for(rank in RANKS.values()) {
                print("${rank.Symbol} ")
        }
        println("\n")

        for(suit in SUITS.values()) {
                print("${suit.Symbol} ")
        }
        println("\n")

        for(suit in SUITS.values())
                for(rank in RANKS.values()) {
                        print("${rank.Symbol}${suit.Symbol} ")
                }
}
