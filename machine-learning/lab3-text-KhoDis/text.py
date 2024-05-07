import tensorflow as tf


with open('text.txt', 'r') as file:
    text = file.read()

print(f'The text contains {len(text)} characters.')
vocab = sorted(set(text))
print(f'The vocabulary contains {len(vocab)} characters.')
print(f'The vocabulary is: {vocab}')

ids_from_chars = tf.keras.layers.StringLookup(vocabulary=list(vocab), mask_token=None)
chars_from_ids = tf.keras.layers.StringLookup(vocabulary=ids_from_chars.get_vocabulary(),
                                              invert=True, mask_token=None)


def text_from_ids(ids):
    return tf.strings.reduce_join(chars_from_ids(ids), axis=-1)


all_ids = ids_from_chars(tf.strings.unicode_split(text, 'UTF-8'))
print(f'First 250 ids: {all_ids[:250]=}')
print(f'Shape of all_ids: {all_ids.shape=}')

seq_length = 100
sequences = tf.data.Dataset.from_tensor_slices(all_ids).batch(seq_length + 1, drop_remainder=True)


def split_input_target(sequence):
    input_text = sequence[:-1]
    target_text = sequence[1:]
    return input_text, target_text


dataset = sequences.map(split_input_target).batch(64, drop_remainder=True).prefetch(tf.data.experimental.AUTOTUNE)

print(dataset)

vocab_size = len(ids_from_chars.get_vocabulary())
embedding_dim = 256
rnn_units = 1024


class LSTMModel(tf.keras.Model):
    def __init__(self, vocab_size, embedding_dim, rnn_units):
        super().__init__(self)
        self.embedding = tf.keras.layers.Embedding(vocab_size, embedding_dim)
        self.lstm = tf.keras.layers.LSTM(rnn_units, return_sequences=True, return_state=True)
        self.dense = tf.keras.layers.Dense(vocab_size)

    def call(self, inputs, states=None, return_state=False, training=False):
        x = inputs
        x = self.embedding(x, training=training)
        if states is None:
            states = self.lstm.get_initial_state(x)
        x, state_h, state_c = self.lstm(x, initial_state=states, training=training)
        x = self.dense(x, training=training)

        if return_state:
            return x, [state_h, state_c]
        else:
            return x


model = LSTMModel(vocab_size=vocab_size, embedding_dim=embedding_dim, rnn_units=rnn_units)

loss = tf.losses.SparseCategoricalCrossentropy(from_logits=True)

model.compile(optimizer='adam', loss=loss)

history = model.fit(dataset, epochs=20)

# Save the weights
model.save_weights('./lstm_weights.h5')
