package indigo

import kotlin.random.Random

fun main() {

        println("Indigo Card Game")
        Table.clarifyWhoPlaysFirst()
        Table.printInitialTableStatus()

        // play the game until all 52 cards have been played
        while(!Table.deckIsEmpty() || !Human.handIsEmpty() || !Computer.handIsEmpty()) {
                Table.printTableStatus()
                Table.activePlayer!!.printHand()
                if(Table.activePlayer!!.clarifyWhichCardToPlayNext() == -1) {
                        println("Game Over")
                        return //
                }
                Table.activePlayer!!.throwCardFromHandToStaple()
                Table.activePlayer!!.getSixNewCardsFromDeckIfHandIsEmpty()
                // check for win condition
                if(Table.playerWonTheStapleLastRound()) {
                        Table.activePlayer!!.wonCards.addAll(Table.staple)
                        Table.lastWinner = Table.activePlayer!!
                        println("${Table.activePlayer!!} wins cards")
                        Table.staple.clear()
                        Table.printPoints(Human, Computer)
                }
                // handle last round without wins by deciding which player gets remaining cards from the staple
                if(Table.deckIsEmpty() && Human.handIsEmpty() && Computer.handIsEmpty()) {
                        if(Table.lastWinner != null)
                                Table.lastWinner!!.wonCards.addAll(Table.staple)
                        else
                                Table.playerStartingFirst!!.wonCards.addAll(Table.staple)
                }
                Table.switchActivePlayer()
        }
        Table.printTableStatus()
        Table.assignThreeExtraPointsToThePlayerWithMostCardsWon(Human, Computer)
        Table.printPoints(Human, Computer)
        println("Game Over")
}

object Table {

        var deck = mutableListOf<Card>()        // not yet played cards
        var staple = mutableListOf<Card>()      // already played cards
        var playerStartingFirst:Player? = null
        var activePlayer:Player? = null
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

        fun clarifyWhoPlaysFirst() {
                while(playerStartingFirst == null) {
                        print("Play first?\n> ")
                        val answer = readLine()!!
                        if( answer == "yes") {
                                playerStartingFirst = Human
                                activePlayer = Human
                        }
                        else if (answer == "no") {
                                playerStartingFirst = Computer
                                activePlayer = Computer
                        }
                }
        }

        fun switchActivePlayer() {
                if (activePlayer == Human)
                        activePlayer = Computer
                else
                        activePlayer = Human
        }

        fun deckIsEmpty():Boolean {
                return deck.size == 0
        }

        fun stapleIsEmpty():Boolean {
                return staple.size == 0
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

        fun playerWonTheStapleLastRound(): Boolean {
                if(staple.size == 1) return false               // first card in that round
                val previousCard = staple[staple.size-2]
                return staple.last().winsWith(previousCard)
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
                else
                        playerStartingFirst!!.extraPoints = 3
        }
}

open class Player {

        var currentHand = mutableListOf<Card>()
        var wonCards = mutableListOf<Card>()
        var extraPoints = 0
        var cardToPlayNext: Card? = null

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

        open fun printHand() {
        }

        open fun clarifyWhichCardToPlayNext():Int {
                return 0
        }

        open fun throwCardFromHandToStaple() {
                Table.staple.add(cardToPlayNext!!)
                currentHand.remove(cardToPlayNext)
        }
}

object Human: Player() {

        override fun printHand() {
                print("Cards in hand: ")
                for(i in 0 until currentHand.size)
                        print("${i+1})${currentHand[i]} ")
                println()
        }

        override fun clarifyWhichCardToPlayNext():Int {
                var chosenCardIndexForNextMove = 0
                while(chosenCardIndexForNextMove == 0 || chosenCardIndexForNextMove !in 1..currentHand.size) {
                        print("Choose a card to play (1-${currentHand.size}):\n> ")
                        val humanCardInput:String = readLine()!!
                        if(humanCardInput == "exit") { return -1 }
                        chosenCardIndexForNextMove = try {
                                humanCardInput.toInt()
                        } catch (e:Exception) {
                                0
                        }
                }
                cardToPlayNext = currentHand[chosenCardIndexForNextMove-1]
                return 0 // means nothing, but need to return a number that is not -1
        }

        override fun toString():String {
                return "Player"
        }
}

object Computer: Player() {

        override fun printHand() {
                for(card in currentHand)
                        print("$card ") // calls overridden toString()
                println()
        }

        override fun clarifyWhichCardToPlayNext():Int {

                val candidates = getCandidateCards()
                val sameSuitsFromHand = getCardsWithTheirSuitNotAloneInTheList(currentHand)
                val sameRanksFromHand = getCardsWithTheirRankNotAloneInTheList(currentHand)
                val sameSuitsFromCandidates = getCardsWithTheirSuitNotAloneInTheList(candidates)
                val sameRanksFromCandidates = getCardsWithTheirRankNotAloneInTheList(candidates)

                // 1) If there is only one card in hand, put it on the table
                if(currentHand.size == 1)
                        cardToPlayNext = currentHand.first()
                // 2) If there is only one candidate card, put it on the table
                else if(candidates.size == 1)
                        cardToPlayNext = candidates.first()
                // 3) If there are no cards on the table:
                else if(Table.stapleIsEmpty()) {
                        if(sameSuitsFromHand.size > 0)
                                cardToPlayNext = sameSuitsFromHand.first()
                        else if(sameRanksFromHand.size > 0)
                                cardToPlayNext = sameRanksFromHand.first()
                        else
                                cardToPlayNext = currentHand.first()
                }
                // 4) If there are cards on the table but no candidate cards, use the same tactics as in step 3.
                else if(!Table.stapleIsEmpty() && candidates.size == 0) {
                        if(sameSuitsFromHand.size > 0)
                                cardToPlayNext = sameSuitsFromHand.first()
                        else if(sameRanksFromHand.size > 0)
                                cardToPlayNext = sameRanksFromHand.first()
                        else
                                cardToPlayNext = currentHand.first()
                }
                // 5) If there are two or more candidate cards:
                else if(candidates.size >= 2) {
                        if(sameSuitsFromCandidates.size >= 2)
                                cardToPlayNext = sameSuitsFromCandidates.first()
                        else if(sameRanksFromCandidates.size >= 2)
                                cardToPlayNext = sameRanksFromCandidates.first()
                        else
                                cardToPlayNext = candidates.first()
                }
                // return value means nothing, but need to return a number that is not -1
                return 0
        }

        override fun throwCardFromHandToStaple() {
                super.throwCardFromHandToStaple()
                println("Computer plays ${Table.staple.last()}")
        }

        override fun toString():String {
                return "Computer"
        }

        fun getCandidateCards():MutableList<Card> {
                val myCandidates = mutableListOf<Card>()
                if(Table.staple.size >=1)
                        for(cardFromHand in currentHand)
                                if(cardFromHand.winsWith(Table.staple.last()) && !myCandidates.contains(cardFromHand))
                                        myCandidates.add(cardFromHand)
                return myCandidates
        }

        fun getCardsWithTheirSuitNotAloneInTheList(cardList:MutableList<Card>):MutableList<Card> {
                val sameSuitCards = mutableListOf<Card>()
                for(suit in SUITS.values()) {
                        val tmp = getCardsWithMatchingSuit(suit, cardList)
                        if(tmp.size > 1)
                                sameSuitCards.addAll(tmp)
                }
                return sameSuitCards
        }

        fun getCardsWithTheirRankNotAloneInTheList(cardList:MutableList<Card>):MutableList<Card> {
                val sameRankCards = mutableListOf<Card>()
                for(rank in RANKS.values()) {
                        val tmp = getCardsWithMatchingRank(rank, cardList)
                        if(tmp.size > 1)
                                sameRankCards.addAll(tmp)
                }
                return sameRankCards
        }

        fun getCardsWithMatchingSuit(suit:SUITS, cardList:MutableList<Card>):MutableList<Card> {
                val result = mutableListOf<Card>()
                for(card in cardList)
                        if(card.suit == suit)
                                result.add(card)
                return result
        }

        fun getCardsWithMatchingRank(rank:RANKS, cardList:MutableList<Card>):MutableList<Card> {
                val result = mutableListOf<Card>()
                for(card in cardList)
                        if(card.rank == rank)
                                result.add(card)
                return result
        }
}

class Card(var suit: SUITS, var rank: RANKS) {

        override fun toString():String {
                return "${rank.Symbol}${suit.Symbol}"
        }

        fun winsWith(other:Card):Boolean {
                val result = this.rank == other.rank || this.suit == other.suit
                return result
        }
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
