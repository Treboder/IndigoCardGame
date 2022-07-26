import kotlin.random.Random

fun main() {

        println("Indigo Card Game")
        var humanPlaysFirst:Boolean? = null
        while(humanPlaysFirst == null) {
                print("Play first?\n> ")
                var answer = readLine()!!
                if( answer == "yes")
                        humanPlaysFirst = true
                else if (answer == "no")
                        humanPlaysFirst = false
        }

        Deck.shuffle()
        var cardsOnTheTable = Deck.get(4)
        print("Initial cards on the table: ")
        Deck.printCardList(cardsOnTheTable)

        var human = Player()
        human.cards = Deck.get(6)

        var computer = Player()
        computer.cards = Deck.get(6)

        fun Int.isEven(): Boolean {
                if(this % 2 == 0)
                        return true
                else
                        return false
        }
        var round = if(humanPlaysFirst) 0 else 1 // even for humans turn and odd for computers turn
        while(cardsOnTheTable.size < 52)
        {
                println()
                println("${cardsOnTheTable.size} cards on the table, and the top card is ${cardsOnTheTable.last()}")
                if(round.isEven()) {
                        print("Cards in hand: ")
                        human.printCardList()

                        var validCardIndex = 0
                        while(validCardIndex == 0 || !(validCardIndex in 1..human.cards.size)) {
                                print("Choose a card to play (1-${human.cards.size}):\n> ")
                                var humanCardInput:String = readLine()!!

                                if(humanCardInput == "exit") {println("Game Over"); return }

                                try{ validCardIndex = humanCardInput.toInt()} catch (e:Exception) { validCardIndex = 0}
                        }
                        cardsOnTheTable.add(human.throwCardFromHand(validCardIndex))
                        human.getSixNewCardsFromDeckIfHandIsEmpty()
                }
                else {
                        cardsOnTheTable.add(computer.throwCardFromHandDefaultFirst())
                        println("Computer plays ${cardsOnTheTable.last()}")
                        computer.getSixNewCardsFromDeckIfHandIsEmpty()
                }
                round++
        }
        println()
        println("52 cards on the table, and the top card is ${cardsOnTheTable.last()}")
        println("Game Over")
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
        }

        fun get(numberOfCardsToGet:Int): MutableList<Card> {
                var rnd = Random
                var selectedCards = mutableListOf<Card>()
                if(numberOfCardsToGet!! in 1..52) {
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

                return selectedCards
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

        override fun toString():String {
                return "${rank.Symbol}${suit.Symbol}"
        }
}

class Player() {
        var cards = mutableListOf<Card>()

        fun throwCardFromHand(cardToThrowIndex:Int): Card {
                var cardToThrowObject = cards[cardToThrowIndex-1]
                cards.removeAt(cardToThrowIndex-1)
                return cardToThrowObject
        }

        fun throwCardFromHandDefaultFirst(): Card {
                if(cards.size == 1) {
                        var lastCard = cards[0]
                        cards.clear()
                        return lastCard
                }
                var cardToThrowObject = cards.last()
                cards.removeLast()
                return cardToThrowObject
        }

        fun throwCardFromHandRandomly(): Card {
                if(cards.size == 1) {
                        var lastCard = cards[0]
                        cards.clear()
                        return lastCard
                }
                var rnd = Random
                var cardToThrowIndex = rnd.nextInt(0,cards.size)
                var cardToThrowObject = cards[cardToThrowIndex]
                cards.removeAt(cardToThrowIndex)
                return cardToThrowObject
        }

        fun getSixNewCardsFromDeckIfHandIsEmpty() {
                if(cards.size == 0 && Deck.cards.size >=6)
                        cards.addAll(Deck.get(6))
        }

        fun printCardList() {
                for(i in 0..cards.size-1)
                        print("${i+1})${cards[i]} ")
                println()
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