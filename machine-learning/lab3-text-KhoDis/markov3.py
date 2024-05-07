import numpy as np


# function to train the Markov chain
def train_markov_chain(text, n):
    chain = {}
    for i in range(len(text) - n):
        state = text[i:i + n]
        next_char = text[i + n]
        chain.setdefault(state, {}).setdefault(next_char, 0)
        chain[state][next_char] += 1
    # convert the counts to probabilities
    for state, transitions in chain.items():
        total = sum(transitions.values())
        for char in transitions:
            transitions[char] /= total
    return chain


# function to generate text using the Markov chain
def generate_markov_text(chain, length):
    state = np.random.choice(list(chain.keys()))
    text = state
    for i in range(length):
        if state not in chain:
            break
        # get the probabilities for the next character based on the current state
        probs = list(chain[state].values())
        # choose the next character randomly based on the probabilities
        next_char = np.random.choice(list(chain[state].keys()), p=probs)
        # update the state with the new character
        state = state[1:] + next_char
        # add the new character to the generated text
        text += next_char
    return text


# train the Markov chain with n=10
n = 10
with open('text.txt', 'r') as file:
    text = file.read()
chain = train_markov_chain(text, n)

# generate some text using the Markov chain
markov_text = generate_markov_text(chain, length=500)
print(markov_text)
