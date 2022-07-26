import kotlin.random.Random

fun main() {

        println("Indigo Card Game")
        Table.clarifyWhoPlaysFirst()
        Table.printInitialTableStatus()

        var human = Player()
        var computer = Player()

        // play the game until all 52 cards have been played
        while(!Table.deckIsEmpty() || !human.handIsEmpty() || !computer.handIsEmpty()) {
                println()
                Table.printTableStatus()
                if(Table.isHumanTurn()) {
                        human.printHandWithOptionsToPlay()
                        human.clarifyWhichCardToPlay()
                        if(human.choseExit()) {
                                println("Game Over");
                                return
                        }
                        human.throwCardFromHandToStapleAfterChoice()
                        human.getSixNewCardsFromDeckIfHandIsEmpty()
                }
                else { // computers turn
                        computer.throwCardFromHandToTableDefault()
                        println("Computer plays ${Table.staple.last()}")
                        computer.getSixNewCardsFromDeckIfHandIsEmpty()
                }

                // check for win
                if(Table.playerWonTheStapleLastRound()) {
                        if(Table.lastTurnWasHuman()) {
                                human.wonCards.addAll(Table.staple)
                                Table.lastWinner = human
                                println("Player wins cards")
                        }
                        else {
                                computer.wonCards.addAll(Table.staple)
                                Table.lastWinner = computer
                                println("Computer wins cards")
                        }
                        Table.staple.clear()
                        Table.printPoints(human, computer)
                }

                // handle last round without wins
                if(Table.deckIsEmpty() && human.handIsEmpty() && computer.handIsEmpty()) {
                        // decide which player gets remaining cards on the staple
                        if(Table.lastWinner == null && Table.humanPlaysFirst!!)
                                human.wonCards.addAll(Table.staple)
                        else if(Table.lastWinner == null && !Table.humanPlaysFirst!!)
                                computer.wonCards.addAll(Table.staple)
                        else
                                Table.lastWinner!!.wonCards.addAll(Table.staple)
                }
        }
        println()
        Table.printTableStatus()
        Table.setExtraPoints(human, computer)
        Table.printPoints(human, computer)
        println("Game Over")
}

object Table {

        var deck = mutableListOf<Card>()        // not yet played
        var staple = mutableListOf<Card>()      // already played
        var humanPlaysFirst:Boolean? = null     // not defined until user made the choice
        var round = 0                           // human starts by default
        var lastWinner:Player? = null

        init {
                initializeDeck()
                shuffleDeck()
                staple = dealRandomCardsFromDeck(4)
        }

        fun clarifyWhoPlaysFirst() {
                while(humanPlaysFirst == null) {
                        print("Play first?\n> ")
                        var answer = readLine()!!
                        if( answer == "yes")
                                humanPlaysFirst = true
                        else if (answer == "no")
                                humanPlaysFirst = false
                }
                round = if(humanPlaysFirst!!) 0 else 1 // even number for humans turn and odd for computers turn
        }

        fun initializeDeck() {
                for(suit in SUITS.values())
                        for(rank in RANKS.values())
                                deck.add(Card(suit, rank))
        }

        fun shuffleDeck() {
                var rnd = Random
                repeat(52) {
                        var cardIndex1 = rnd.nextInt(0,52)
                        var cardIndex2 = rnd.nextInt(0,52)
                        var swap = deck[cardIndex1]
                        deck[cardIndex1] =  deck[cardIndex2]
                        deck[cardIndex2] =  swap
                }
        }

        fun resetDeck() {
                deck = mutableListOf<Card>()
                initializeDeck()
                println("Card deck is reset.")
        }

        fun deckIsEmpty():Boolean {
                if(deck.size == 0)
                        return true
                else
                        return false
        }

        fun stapleIsEmpty():Boolean {
                if(staple.size == 0)
                        return true
                else
                        return false
        }

        fun dealRandomCardsFromDeck(numberOfCardsToGet:Int): MutableList<Card> {
                var rnd = Random
                var selectedCards = mutableListOf<Card>()
                if(numberOfCardsToGet!! in 1..52) {
                        if(numberOfCardsToGet <= deck.count()) {
                                while(selectedCards.count() < numberOfCardsToGet) {
                                        var index = rnd.nextInt(0,deck.count())
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

        fun playerWonTheStapleLastRound(): Boolean {
                if(staple.size == 1) return false // first card in that round
                val previousCard = staple[staple.size-2]
                if(staple.last().rank == previousCard.rank || staple.last().suit == previousCard.suit)
                        return true
                else
                        return false
        }

        fun printInitialTableStatus() {
                print("Initial cards on the table: ")
                for(card in staple)
                        card.print()
                println()
        }

        fun printTableStatus() {
                if(staple.size == 0)
                        println("No cards on the table")
                else
                        println("${staple.size} cards on the table, and the top card is ${staple.last()}")
        }

        fun printPoints(human:Player, computer: Player) {
                println("Score: Player ${human.calculatePoints()} - Computer ${computer.calculatePoints()}")
                println("Cards: Player ${human.wonCards.size} - Computer ${computer.wonCards.size}")
        }

        fun setExtraPoints(human:Player, computer:Player) {

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
                if(round.isEven())
                        return true
                else
                        return false
        }

        fun lastTurnWasHuman(): Boolean {
                return !isHumanTurn()
        }

        fun Int.isEven(): Boolean {
                if(this % 2 == 0)
                        return true
                else
                        return false
        }
}

class Player() {

        var currentHand = mutableListOf<Card>()
        var wonCards = mutableListOf<Card>()
        var chosenCardIndexForNextMove = 0
        var extraPoints = 0

        init {
                currentHand = Table.dealRandomCardsFromDeck(6)
        }

        fun handIsEmpty():Boolean {
                if(currentHand.size == 0)
                        return true
                else
                        return false
        }

        fun clarifyWhichCardToPlay() {
                chosenCardIndexForNextMove = 0
                while(chosenCardIndexForNextMove == 0 || !(chosenCardIndexForNextMove in 1..currentHand.size)) {
                        print("Choose a card to play (1-${currentHand.size}):\n> ")
                        var humanCardInput:String = readLine()!!
                        if(humanCardInput == "exit") { chosenCardIndexForNextMove = -1; return}
                        try{ chosenCardIndexForNextMove = humanCardInput.toInt()} catch (e:Exception) { chosenCardIndexForNextMove = 0}
                }
        }

        fun choseExit():Boolean {
                if(chosenCardIndexForNextMove == -1 )
                        return true
                else
                        return false
        }

        fun throwCardFromHandToStapleAfterChoice() {
                Table.staple.add(currentHand[chosenCardIndexForNextMove-1])
                currentHand.removeAt(chosenCardIndexForNextMove-1)
                Table.round++
        }

        fun throwCardFromHandToTableDefault() {
                Table.staple.add(currentHand.first())
                currentHand.removeFirst()
                Table.round++
        }

        fun throwCardFromHandToTableRandomly() {
                var cardToThrowIndex = Random.nextInt(0, currentHand.size)
                Table.staple.add(currentHand[cardToThrowIndex])
                currentHand.removeAt(cardToThrowIndex)
                Table.round++
        }

        fun getSixNewCardsFromDeckIfHandIsEmpty() {
                if(currentHand.size == 0 && Table.deck.size >=6)
                        currentHand.addAll(Table.dealRandomCardsFromDeck(6))
        }

        fun printHandWithOptionsToPlay() {
                print("Cards in hand: ")
                for(i in 0..currentHand.size-1)
                        print("${i+1})${currentHand[i]} ")
                println()
        }

        fun calculatePoints(): Int {
                var sum = 0
                for(card in wonCards)
                        sum += card.rank.Point
                return sum + extraPoints
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

enum class RANKS(val Symbol:String, Rank: Int, val Point: Int) {
        ACE("A", 1, 1),
        TWO("2", 2, 0),
        THREE("3", 3, 0),
        FOUR("4", 4, 0),
        FIVE("5", 5, 0),
        SIX("6", 6, 0),
        SEVEN("7", 7, 0),
        EIGHT("8", 8, 0),
        NINE("9", 9, 0),
        TEN("10", 10, 1),
        JACK("J", 11, 1),
        QUEEN("Q", 12, 1),
        KING("K", 13, 1),
}

enum class SUITS(val CodePoint: Char, val Symbol:String, Rank: Int) {
        CLUB('\u2663', Character.toString('\u2663'), 0),
        SPADE('\u2660', Character.toString('\u2660'), 1),
        HEART('\u2665', Character.toString('\u2665'), 2),
        DIAMOND('\u2666', Character.toString('\u2666'), 3)
}
