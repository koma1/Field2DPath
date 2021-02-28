Optimal (less steps) way finder on 2D-field between two poins

Formulation of task

Implement an algorithm for finding the optimal route between two points in a Cartesian field

Algorithm: from point A we begin to "put down" in adjacent (left, right, top, bottom)
cells - the number of moves + 1 from the current one. When the required point is in the required
cell, create a "list" from the path (with a reverse search from the found point to the initial one)

Реализовать алгоритм поиска оптимального маршрута между двумя точками в декартовом поле

Алгоритм: от точки А начинаем "проставлять" в соседние (левая, правая, верхняя, нижняя)
ячейки - количество ходов + 1 от текущей. Когда в искомой клетке окажется искомая 
точка, создаем "список" из пути (с обратным поиском от найденой точки к начальной)