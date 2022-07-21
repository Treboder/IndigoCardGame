package indigo

fun main() {

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