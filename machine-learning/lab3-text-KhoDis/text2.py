import tensorflow as tf

text = open('text.txt', 'r').read()

vocab = sorted(set(text))

ids_from_chars = tf.keras.layers.StringLookup(vocabulary=list(vocab), mask_token=None)
chars_from_ids = tf.keras.layers.StringLookup(vocabulary=ids_from_chars.get_vocabulary(),
                                              invert=True, mask_token=None)


def text_from_ids(ids):
    return tf.strings.reduce_join(chars_from_ids(ids), axis=-1)


seq_length = 100
sequences = tf.data.Dataset.from_tensor_slices(
    ids_from_chars(tf.strings.unicode_split(text, 'UTF-8'))
).batch(seq_length + 1, drop_remainder=True)

for seq in sequences.take(1):
    print("Sequence", chars_from_ids(seq).numpy())


def split_input_target(sequence):
    input_text = sequence[:-1]
    target_text = sequence[1:]
    return input_text, target_text


dataset = (
    sequences.map(split_input_target)
    .batch(64, drop_remainder=True)
    .prefetch(tf.data.experimental.AUTOTUNE))


class LSTMModel(tf.keras.Model):
    def __init__(self, vocab_size, embedding_dim, rnn_units):
        super().__init__(self)
        self.embedding = tf.keras.layers.Embedding(vocab_size, embedding_dim)
        self.lstm = tf.keras.layers.LSTM(rnn_units,
                                         return_sequences=True,
                                         return_state=True)
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


vocab_size = len(ids_from_chars.get_vocabulary())
embedding_dim = 256
rnn_units = 1024

model = LSTMModel(vocab_size=vocab_size, embedding_dim=embedding_dim, rnn_units=rnn_units)

input_example_batch, target_example_batch = next(iter(dataset))
example_batch_predictions = model(input_example_batch)
batch_size, sequence_length, vocab_size = example_batch_predictions.shape
print(f"{batch_size} x {sequence_length} x {vocab_size}")

model.load_weights('./lstm_weights.h5')


class OneStep(tf.keras.Model):
    def __init__(self, model, chars_from_ids, ids_from_chars, temperature=1.0):
        super().__init__()
        self.temperature = temperature
        self.model = model
        self.chars_from_ids = chars_from_ids
        self.ids_from_chars = ids_from_chars

        skip_ids = self.ids_from_chars(['[UNK]'])[:, None]
        sparse_mask = tf.SparseTensor(
            values=[-float('inf')] * len(skip_ids),
            indices=skip_ids,
            dense_shape=[len(ids_from_chars.get_vocabulary())])
        self.prediction_mask = tf.sparse.to_dense(sparse_mask)

    @tf.function
    def generate_one_step(self, inputs, states=None):
        input_chars = tf.strings.unicode_split(inputs, 'UTF-8')
        input_ids = self.ids_from_chars(input_chars).to_tensor()

        predicted_logits, states = self.model(inputs=input_ids, states=states, return_state=True)
        predicted_logits = predicted_logits[:, -1, :]
        predicted_logits /= self.temperature
        predicted_logits += self.prediction_mask

        predicted_ids = tf.random.categorical(predicted_logits, num_samples=1)
        predicted_ids = tf.squeeze(predicted_ids, axis=-1)

        predicted_chars = self.chars_from_ids(predicted_ids)

        return predicted_chars, states


class TextGenerator:
    def __init__(self, model, chars_from_ids, ids_from_chars, temperature=1.0):
        self.one_step_model = OneStep(model, chars_from_ids, ids_from_chars, temperature)

    def generate_text(self, prompt, num_generate=100):
        states = None
        next_char = tf.constant([prompt])
        result = [next_char]

        for n in range(num_generate):
            next_char, states = self.one_step_model.generate_one_step(next_char, states=states)
            result.append(next_char)

        result = tf.strings.join(result)
        return result[0].numpy().decode('utf-8')


text_generator = TextGenerator(model, chars_from_ids, ids_from_chars, temperature=1.0)

prompt = 'JOHN:'
generated_text = text_generator.generate_text(prompt, num_generate=500)
print(generated_text)
