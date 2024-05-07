import tensorflow as tf
from tensorflow import keras
import matplotlib.pyplot as plt

(train_images_mnist, train_labels_mnist), (
test_images_mnist, test_labels_mnist) = keras.datasets.mnist.load_data()
mnist_model = tf.keras.models.load_model('task1/mnist_model.h5')

train_images_mnist = train_images_mnist.reshape((60000, 28, 28, 1)).astype('float32') / 255.0
test_images_mnist = test_images_mnist.reshape((10000, 28, 28, 1)).astype('float32') / 255.0

(train_images_fashion, train_labels_fashion), (
test_images_fashion, test_labels_fashion) = keras.datasets.fashion_mnist.load_data()

train_images_fashion = train_images_fashion.reshape((60000, 28, 28, 1)).astype('float32') / 255.0
test_images_fashion = test_images_fashion.reshape((10000, 28, 28, 1)).astype('float32') / 255.0

fashion_model = keras.models.Sequential([
    keras.layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1),
                        weights=mnist_model.layers[0].get_weights(), trainable=False),
    keras.layers.MaxPooling2D((2, 2)),
    keras.layers.Conv2D(64, (3, 3), activation='relu', weights=mnist_model.layers[2].get_weights(),
                        trainable=False),
    keras.layers.MaxPooling2D((2, 2)),
    keras.layers.Conv2D(64, (3, 3), activation='relu', weights=mnist_model.layers[4].get_weights(),
                        trainable=False),
    keras.layers.Flatten(),
    keras.layers.Dense(64, activation='relu'),
    keras.layers.Dense(10, activation='softmax')
])

fashion_model.compile(optimizer='adam',
                      loss='sparse_categorical_crossentropy',
                      metrics=['accuracy'])

batch_size = 512  # 128
epoch_half = 5  # 3
validation_interval = 10_000 // batch_size

history = fashion_model.fit(train_images_fashion, train_labels_fashion, epochs=epoch_half, batch_size=batch_size)

for layer in fashion_model.layers:
    if isinstance(layer, tf.keras.layers.Conv2D):
        layer.trainable = True

history_unfreeze = fashion_model.fit(train_images_fashion, train_labels_fashion, epochs=epoch_half,
                                     batch_size=batch_size)

fashion_model.save('task5/fashion_model.h5')

fig, ax = plt.subplots(2, 1, figsize=(8, 8))
ax[0].plot(range(0, epoch_half), history.history['loss'], label='Training Loss (Frozen)')
ax[0].plot(range(epoch_half, epoch_half * 2), history_unfreeze.history['loss'], label='Training Loss (Unfrozen)')
ax[0].set_xlabel('Epoch')
ax[0].set_ylabel('Loss')
ax[0].legend()
ax[1].plot(range(0, epoch_half), history.history['accuracy'], label='Training Accuracy (Frozen)')
ax[1].plot(range(epoch_half, epoch_half * 2), history_unfreeze.history['accuracy'], label='Training Accuracy (Unfrozen)')
ax[1].set_xlabel('Epoch')
ax[1].set_ylabel('Accuracy')
ax[1].legend()
plt.savefig('task5/learning_curves.png')
plt.clf()
