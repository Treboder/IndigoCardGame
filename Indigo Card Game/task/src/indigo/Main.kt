package indigo

import kotlin.random.Random

fun main() {

        println("Indigo Card Game")
        Table.clarifyWhoPlaysFirst()
        Table.printInitialTableStatus()

        val human = Human()
        val computer = Computer()

        // play the game until all 52 cards have been played
        while(!Table.deckIsEmpty() || !human.handIsEmpty() || !computer.handIsEmpty()) {
                Table.printTableStatus()
                if(Table.isHumanTurn()) {
                        human.printHandWithOptionsToPlay()
                        if(human.clarifyWhichCardToPlay() == -1) {
                                println("Game Over")
                                return
                        }
                        human.throwCardFromHandToStaple()
                        human.getSixNewCardsFromDeckIfHandIsEmpty()
                }
                else { // computers turn
                        computer.throwCardFromHandToStaple()
                        println("Computer plays ${Table.staple.last()}")
                        computer.getSixNewCardsFromDeckIfHandIsEmpty()
                }

                // check for win
                if(Table.anyPlayerWonTheStapleLastRound()) {
                        if(Table.lastTurnWasHuman()) {
                                human.wonCards.addAll(Table.staple)
                                Table.lastWinner = human
                                println("Player wins cards")
                        }
                        else { // last turn was computer
                                computer.wonCards.addAll(Table.staple)
                                Table.lastWinner = computer
                                println("Computer wins cards")
                        }
                        Table.staple.clear()
                        Table.printPoints(human, computer)
                }

                // handle last round without wins
                if(Table.deckIsEmpty() && human.handIsEmpty() && computer.handIsEmpty()) {
                        // decide which player gets remaining cards from the staple
                        if(Table.lastWinner == null && Table.humanPlaysFirst())
                                human.wonCards.addAll(Table.staple)
                        if(Table.lastWinner == null && Table.computerPlaysFirst())
                                computer.wonCards.addAll(Table.staple)
                        if(Table.lastWinner != null)
                                Table.lastWinner!!.wonCards.addAll(Table.staple)
                }
        }
        Table.printTableStatus()
        Table.assignThreeExtraPointsToThePlayerWithMostCardsWon(human, computer)
        Table.printPoints(human, computer)
        println("Game Over")
}

object Table {

        var deck = mutableListOf<Card>()        // not yet played
        var staple = mutableListOf<Card>()      // already played
        private var humanPlaysFirst:Boolean? = null     // not defined until user made the choice
        var round = 0                           // human starts by default
        var lastWinner: Player? = null

        init {
                initializeDeck()
                shuffleDeck()
                staple = dealRandomCardsFromDeck(4)
        }

        private fun initializeDeck() {
                for(suit in SUITS.values())
                        for(rank in RANKS.values())
                                deck.add(Card(suit, rank))
        }

        private fun shuffleDeck() {
                val rnd = Random
                repeat(52) {
                        val cardIndex1 = rnd.nextInt(0,52)
                        val cardIndex2 = rnd.nextInt(0,52)
                        val swap = deck[cardIndex1]
                        deck[cardIndex1] =  deck[cardIndex2]
                        deck[cardIndex2] =  swap
                }
        }

        private fun Int.isEven(): Boolean {
                return this % 2 == 0
        }

        fun humanPlaysFirst(): Boolean {
                return humanPlaysFirst == true
        }

        fun computerPlaysFirst(): Boolean {
                return humanPlaysFirst == false
        }

        fun clarifyWhoPlaysFirst() {
                while(humanPlaysFirst == null) {
                        print("Play first?\n> ")
                        val answer = readLine()!!
                        if( answer == "yes")
                                humanPlaysFirst = true
                        else if (answer == "no")
                                humanPlaysFirst = false
                }
                round = if(humanPlaysFirst!!) 0 else 1 // even number for humans turn and odd for computers turn
        }

        fun deckIsEmpty():Boolean {
                return deck.size == 0
        }

        fun dealRandomCardsFromDeck(numberOfCardsToGet:Int): MutableList<Card> {
                val rnd = Random
                val selectedCards = mutableListOf<Card>()
                if(numberOfCardsToGet in 1..52) {
                        if(numberOfCardsToGet <= deck.count()) {
                                while(selectedCards.count() < numberOfCardsToGet) {
                                        val index = rnd.nextInt(0, deck.count())
                                        selectedCards.add(deck[index])
                                        deck.removeAt(index)
                                }
                        }
                        else
                                println("The remaining cards are insufficient to meet the request.")
                }
                else
                        println("Invalid number of cards.")

                return selectedCards
        }

        fun anyPlayerWonTheStapleLastRound(): Boolean {
                if(staple.size == 1) return false // first card in that round
                val previousCard = staple[staple.size-2]
                return staple.last().rank == previousCard.rank || staple.last().suit == previousCard.suit
        }

        fun printInitialTableStatus() {
                print("Initial cards on the table: ")
                for(card in staple)
                        print("$card ") // calls overridden toString()
                println()
        }

        fun printTableStatus() {
                println()
                if(staple.size == 0)
                        println("No cards on the table")
                else
                        println("${staple.size} cards on the table, and the top card is ${staple.last()}")
        }

        fun printPoints(human: Player, computer: Player) {
                println("Score: Player ${human.calculatePoints()} - Computer ${computer.calculatePoints()}")
                println("Cards: Player ${human.wonCards.size} - Computer ${computer.wonCards.size}")
        }

        fun assignThreeExtraPointsToThePlayerWithMostCardsWon(human: Player, computer: Player) {

                if(human.wonCards.size > computer.wonCards.size)
                        human.extraPoints = 3

                else if (human.wonCards.size < computer.wonCards.size)
                        computer.extraPoints = 3

                else { // same number of cards
                        if(humanPlaysFirst!!)
                                human.extraPoints = 3
                        else
                                computer.extraPoints = 3
                }
        }

        fun isHumanTurn():Boolean {
                return round.isEven()
        }

        fun lastTurnWasHuman(): Boolean {
                return !isHumanTurn()
        }

}

open class Player {

        var currentHand = mutableListOf<Card>()
        var wonCards = mutableListOf<Card>()
        var extraPoints = 0

        init {
                currentHand = Table.dealRandomCardsFromDeck(6)
        }

        fun handIsEmpty():Boolean {
                return currentHand.size == 0
        }

        fun getSixNewCardsFromDeckIfHandIsEmpty() {
                if(currentHand.size == 0 && Table.deck.size >=6)
                        currentHand.addAll(Table.dealRandomCardsFromDeck(6))
        }

        fun calculatePoints(): Int {
                var sum = 0
                for(card in wonCards)
                        sum += card.rank.Point
                return sum + extraPoints
        }

        open fun throwCardFromHandToStaple() {

        }
}
class Human(): Player() {

        private var chosenCardIndexForNextMove = 0

        fun printHandWithOptionsToPlay() {
                print("Cards in hand: ")
                for(i in 0 until currentHand.size)
                        print("${i+1})${currentHand[i]} ")
                println()
        }

        fun clarifyWhichCardToPlay():Int {
                chosenCardIndexForNextMove = 0
                while(chosenCardIndexForNextMove == 0 || chosenCardIndexForNextMove !in 1..currentHand.size) {
                        print("Choose a card to play (1-${currentHand.size}):\n> ")
                        val humanCardInput:String = readLine()!!
                        if(humanCardInput == "exit") { return -1}
                        chosenCardIndexForNextMove = try {
                                humanCardInput.toInt()
                        } catch (e:Exception) {
                                0
                        }
                }
                return chosenCardIndexForNextMove
        }


        override fun throwCardFromHandToStaple() {
                Table.staple.add(currentHand[chosenCardIndexForNextMove-1])
                currentHand.removeAt(chosenCardIndexForNextMove-1)
                Table.round++
        }

}

class Computer: Player() {

        override fun throwCardFromHandToStaple() {
                Table.staple.add(currentHand.first())
                currentHand.removeFirst()
                Table.round++
        }

}

class Card(var suit: SUITS, var rank: RANKS) {

        override fun toString():String {
                return "${rank.Symbol}${suit.Symbol}"
        }

        // ToDo fun sameRankAs(otherCard:Card) {}
        // ToDo fun sameSuitAs(otherCard:Card) {}
}

enum class RANKS(val Symbol:String, val Point: Int) {
        ACE("A", 1),
        TWO("2", 0),
        THREE("3", 0),
        FOUR("4", 0),
        FIVE("5", 0),
        SIX("6", 0),
        SEVEN("7", 0),
        EIGHT("8", 0),
        NINE("9", 0),
        TEN("10", 1),
        JACK("J", 1),
        QUEEN("Q", 1),
        KING("K", 1),
}

enum class SUITS(val Symbol:String) {
        CLUB('\u2663'.toString()),
        SPADE('\u2660'.toString()),
        HEART('\u2665'.toString()),
        DIAMOND('\u2666'.toString())
}
