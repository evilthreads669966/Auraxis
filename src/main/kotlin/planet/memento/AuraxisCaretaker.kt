package planet.memento

object AuraxisCaretaker {
    private val mementos = mutableListOf<AuraxisMemento>()

    fun restore(index: Int): AuraxisMemento = mementos[index]
    fun save(memento: AuraxisMemento) {
        mementos.add(memento)
    }
}