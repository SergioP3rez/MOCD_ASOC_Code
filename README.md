# MOCD_ASOC_Code
Community detection in social networks is becoming one of the key tasks in social network analysis, since it helps analyzing groups of users with similar interests. This task is also useful in different areas, such as biology (interactions of genes and proteins), psychology (diagnostic criteria), or criminology (fraud detection). This paper presents a metaheuristic approach based on Variable Neighborhood Search (VNS) which leverages the combination of quality and diversity of a constructive procedure inspired in Greedy Randomized Adaptative Search Procedure (GRASP) for detecting communities in social networks. In this work, the community detection problem is modeled as a bi-objective optimization problem, where the two objective functions to be optimized are the Negative Ratio Association (NRA) and Ratio Cut (RC), two objectives that have already been proven to be in conflict. To evaluate the quality of the obtained solutions, we use the Normalized Mutual Information (NMI) metric for the instances under evaluation whose optimal solution is known, and modularity for those in which the optimal solution is unknown. Furthermore, we use metrics widely used in multi-objective optimization community to evaluate solutions, such as coverage, $\epsilon$-indicator, hypervolume, and inverted generational distance. The obtained results outperform the state-of-the-art method for community detection over a set of real-life instances in both, quality and computing time.
